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
import android.content.DialogInterface;
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
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;


import java.io.IOException;
import java.util.ArrayList;

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

    /** Tag for the {@link Log}. */
    private static final String TAG = "AttackDemoActivity";

    // Chart Initials
    BarChart chart ;
    ArrayList<BarEntry> BARENTRY;
    BarDataSet Bardataset;
    BarData BARDATA;

    private TextView modelText;
    private PaintView paintView;
    private ImageClassifier classifier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the model and labels.
        try {
            classifier = new ImageClassifierNormal(this);
        } catch (IOException e) {
            Log.e(TAG, "Failed to initialize an image classifier.", e);
        }
        //startBackgroundThread();

        setContentView(R.layout.activity_main);
        paintView = (PaintView) findViewById(R.id.paintView);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        ProgressBar predictionBar = (ProgressBar) findViewById(R.id.predictionBar);
        BarChart barChart = (BarChart) findViewById(R.id.barChart);
        barChart.animateY(3000);
        barChart.getXAxis().setEnabled(true);
        barChart.getAxisRight().setEnabled(false);
        barChart.getAxisLeft().setAxisMinimum(0.0f); // start at zero
        barChart.getAxisLeft().setAxisMaximum(1.0f); // the axis maximum is 100
        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(false);

        // the labels that should be drawn on the XAxis
        final String[] barLabels = new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};

        IAxisValueFormatter formatter = new IAxisValueFormatter() {

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return barLabels[(int) value];
            }
        };

        barChart.getXAxis().setGranularity(0f); // minimum axis-step (interval) is 1
        barChart.getXAxis().setValueFormatter(formatter);
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.getXAxis().setTextSize(5f);

        BARENTRY = new ArrayList<>();
        initializeBARENTRY();
        //AddValuesToBarEntryLabels();
        Bardataset = new BarDataSet(BARENTRY, "project");
        //Bardataset = new BarDataSet(BARENTRY);
        BARDATA = new BarData(Bardataset);
        barChart.setData(BARDATA);


        paintView.init(metrics, classifier, barChart);

        //final ProgressBar predictionBar = (ProgressBar) findViewById(R.id.predictionBar);
        final TextView modelText = (TextView) findViewById(R.id.modelType);
        modelText.setText("Normal");
        SeekBar seekBar = (SeekBar) findViewById(R.id.strokeThickness);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                modelText.setText(String.valueOf(progress));
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

        /*
        Switch modelSwitch = (Switch) findViewById(R.id.modelSwitch);
        modelSwitch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                paintView.clear();
            }
        });
        */
    }

    public void initializeBARENTRY() {
        for (int j = 0; j < 10; ++j) {
            BARENTRY.add(new BarEntry(j, 0.1f));
        }
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
                classifier.close();
                try {
                    classifier = new ImageClassifierNormal(this);
                    Log.v(TAG, "Initialized Normal classifier.");
                } catch (IOException e) {
                    Log.e(TAG, "Failed to initialize an image classifier.", e);
                }
                //modelText.setText("Normal");
                //paintView.normal();
                paintView.setClassifier(classifier);
                return true;
            case R.id.PGD:
                classifier.close();
                try {
                    classifier = new ImageClassifierPGD(this);
                    Log.v(TAG, "Initialized Madry classifier.");
                } catch (IOException e) {
                    Log.e(TAG, "Failed to initialize an image classifier.", e);
                }
                //modelText.setText("PGD");
                //paintView.emboss();
                paintView.setClassifier(classifier);
                return true;
            case R.id.CleverHans:
                classifier.close();
                try {
                    classifier = new ImageClassifierCleverHans(this);
                    Log.v(TAG, "Initialized CleverHans classifier.");
                } catch (IOException e) {
                    Log.e(TAG, "Failed to initialize an image classifier.", e);
                }
                //paintView.blur();
                return true;
            case R.id.clear:
                paintView.clear();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}