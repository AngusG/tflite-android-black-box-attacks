/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.basicmultitouch;

import android.app.Activity;
import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;

/**
 * This is an example of keeping track of individual touches across multiple
 * {@link android.view.MotionEvent}s.
 * <p>
 * This is illustrated by a View ({@link TouchDisplayView}) that responds to
 * touch events and draws coloured circles for each pointer, stores the last
 * positions of this pointer and draws them. This example shows the relationship
 * between MotionEvent indices, pointer identifiers and actions.
 *
 * @see android.view.MotionEvent
 */
//public class MainActivity extends AppCompatActivity {
public class MainActivity extends Activity {

    private PaintView paintView;
    private ImageClassifier classifier;

    /** Tag for the {@link Log}. */
    private static final String TAG = "AttackDemoActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the model and labels.
        try {
            classifier = new ImageClassifierDigits(this);
        } catch (IOException e) {
            Log.e(TAG, "Failed to initialize an image classifier.", e);
        }
        //startBackgroundThread();

        setContentView(R.layout.activity_main);
        paintView = (PaintView) findViewById(R.id.paintView);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        ProgressBar predictionBar = (ProgressBar) findViewById(R.id.predictionBar);
        paintView.init(metrics, classifier, predictionBar);

        //final ProgressBar predictionBar = (ProgressBar) findViewById(R.id.predictionBar);

        SeekBar seekBar = (SeekBar) findViewById(R.id.strokeThickness);
        final TextView seekBarValue = (TextView) findViewById(R.id.seekBarValue);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBarValue.setText(String.valueOf(progress));
                paintView.setStrokeWidth(progress);
                //predictionBar.setProgress(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        Button resetButton = (Button) findViewById(R.id.resetButton);
        resetButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                paintView.clear();
            }
        });

        Switch modelSwitch = (Switch) findViewById(R.id.modelSwitch);
        modelSwitch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                paintView.clear();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.normal:
                paintView.normal();
                return true;
            case R.id.emboss:
                paintView.emboss();
                return true;
            case R.id.blur:
                paintView.blur();
                return true;
            case R.id.clear:
                paintView.clear();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}