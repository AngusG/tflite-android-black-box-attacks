package com.example.android.basicmultitouch;

import android.app.Activity;
import android.graphics.Color;

import java.io.IOException;

/**
 * Created by gallowaa on 26/03/18.
 */

public class ImageClassifierPGD extends ImageClassifier {

    /**
     * An array to hold inference results, to be feed into Tensorflow Lite as outputs.
     * This isn't part of the super class, because we need a primitive array here.
     */
    private float[][] labelProbArray = null;

    /**
     * Initializes an {@code ImageClassifier}.
     *
     * @param activity
     */
    ImageClassifierPGD(Activity activity) throws IOException {
        super(activity);
        labelProbArray = new float[1][10];
    }

    @Override
    protected String getModelPath() {
        return "public.tflite";
    }

    @Override
    protected String getLabelPath() {
        return "labels.txt";
    }

    @Override
    protected int getImageSizeX() {
        return 28;
    }

    @Override
    protected int getImageSizeY() {
        return 28;
    }

    @Override
    protected int getNumBytesPerChannel() {
        // the non-quantized model uses a 4 bytes
        return 4;
    }

    @Override
    protected void addPixelValue(int pixelValue) {
        Float val = 1.0f;
        if (pixelValue == Color.BLACK) {
            val = 0.0f;
        }
        imgData.putFloat(val);
    }

    @Override
    protected float getProbability(int labelIndex) {
        return labelProbArray[0][labelIndex];
    }

    @Override
    protected void setProbability(int labelIndex, Number value) {
        labelProbArray[0][labelIndex] = value.byteValue();
    }

    @Override
    protected float getNormalizedProbability(int labelIndex) {
        return labelProbArray[0][labelIndex];
    }

    @Override
    protected void runInference() {
        tflite.run(imgData, labelProbArray);
    }
}
