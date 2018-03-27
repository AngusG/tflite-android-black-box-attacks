package com.example.android.basicmultitouch;

import android.app.Activity;
import java.io.IOException;

/**
 * Created by gallowaa on 26/03/18.
 */

public class ImageClassifierDigits extends ImageClassifier {

    /**
     * An array to hold inference results, to be feed into Tensorflow Lite as outputs.
     * This isn't part of the super class, because we need a primitive array here.
     */
    private byte[][] labelProbArray = null;

    /**
     * Initializes an {@code ImageClassifier}.
     *
     * @param activity
     */
    ImageClassifierDigits(Activity activity) throws IOException {
        super(activity);
        labelProbArray = new byte[1][getNumLabels()];
    }

    @Override
    protected String getModelPath() {
        return "natural.tflite";
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
        // the quantized model uses a single byte only
        return 4;
    }

    @Override
    protected void addPixelValue(int pixelValue) {
        imgData.put((byte) ((pixelValue >> 16) & 0xFF));
        imgData.put((byte) ((pixelValue >> 8) & 0xFF));
        imgData.put((byte) (pixelValue & 0xFF));
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
        return (labelProbArray[0][labelIndex] & 0xff);
    }

    @Override
    protected void runInference() {
        tflite.run(imgData, labelProbArray);
    }
}
