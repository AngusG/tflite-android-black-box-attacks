import os
import argparse

import tensorflow as tf
from tensorflow.python.framework import graph_util

dir = os.path.dirname(os.path.realpath(__file__))

from model import Model
# issue 15410
import tempfile
import subprocess
tf.contrib.lite.tempfile = tempfile
tf.contrib.lite.subprocess = subprocess


def freeze_graph(model_folder, output_nodes='y_hat',
                 output_filename='frozen-graph.pb',
                 rename_outputs=None):

    # Load checkpoint
    checkpoint = tf.train.get_checkpoint_state(model_folder)
    input_checkpoint = checkpoint.model_checkpoint_path

    output_graph = output_filename

    # Devices should be cleared to allow Tensorflow to control placement of
    # graph when loading on different machines
    '''    
    saver = tf.train.import_meta_graph(input_checkpoint + '.meta', 
                                       clear_devices=True)
    '''
    img = tf.placeholder(name="img", dtype=tf.float32, shape=[1, 784])
    model = Model(img)
    out = tf.nn.softmax(model.pre_softmax, name="out")

    saver = tf.train.Saver()
    cur_checkpoint = tf.train.latest_checkpoint(input_checkpoint)

    graph = tf.get_default_graph()

    for op in graph.get_operations():
        print(op.name)

    onames = output_nodes.split(',')

    # https://stackoverflow.com/a/34399966/4190475
    if rename_outputs is not None:
        nnames = rename_outputs.split(',')
        with graph.as_default():
            for o, n in zip(onames, nnames):
                _out = tf.identity(graph.get_tensor_by_name(o + ':0'), name=n)
            onames = nnames

    input_graph_def = graph.as_graph_def()

    # fix batch norm nodes
    for node in input_graph_def.node:
        if node.op == 'RefSwitch':
            node.op = 'Switch'
            for index in xrange(len(node.input)):
                if 'moving_' in node.input[index]:
                    node.input[index] = node.input[index] + '/read'
        elif node.op == 'AssignSub':
            node.op = 'Sub'
            if 'use_locking' in node.attr:
                del node.attr['use_locking']

    with tf.Session(graph=graph) as sess:
        saver.restore(sess, input_checkpoint)

        # In production, graph weights no longer need to be updated
        # graph_util provides utility to change all variables to constants
        output_graph_def = graph_util.convert_variables_to_constants(
            sess, input_graph_def,
            onames  # unrelated nodes will be discarded
        )

        # Serialize and write to file
        '''
        with tf.gfile.GFile(output_graph, "wb") as f:
            f.write(output_graph_def.SerializeToString())
        print("%d ops in the final graph." % len(output_graph_def.node))
        '''
        tflite_model = tf.contrib.lite.toco_convert(
            output_graph_def, [img], [out])
        open("public.tflite", "wb").write(tflite_model)


if __name__ == '__main__':
    parser = argparse.ArgumentParser(
        description='Prune and freeze weights from checkpoints into production models')
    parser.add_argument("--ckpt_path",
                        default='ckpt',
                        type=str, help="Path to checkpoint files")
    parser.add_argument("--output_nodes",
                        default='out',
                        type=str, help="Names of output node, comma seperated")
    parser.add_argument("--output_graph",
                        default='frozen-graph.pb',
                        type=str, help="Output graph filename")
    parser.add_argument("--rename_outputs",
                        default=None,
                        type=str, help="Rename output nodes for better \
                        readability in production graph, to be specified in \
                        the same order as output_nodes")
    args = parser.parse_args()

    freeze_graph(args.ckpt_path, args.output_nodes,
                 args.output_graph, args.rename_outputs)
