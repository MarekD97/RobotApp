package com.example.robotapp.services;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class SensorService implements SensorEventListener{
    private final SensorManager mSensorManager;
    private final Sensor mSensor;
    public Handler mHandler;

    private float mMeasure; // acceleration apart from gravity
    private float mMeasureCurrent; // current acceleration including gravity
    private float mMeasureLast; // last acceleration including gravity
    private float[] array = new float[3];
    public int whichSensor;

    public SensorService(Context context, Handler handler, int sensor) {
        //activity =
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(sensor);
        whichSensor = sensor;
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        this.mHandler = handler;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        mMeasureLast = mMeasureCurrent;
        mMeasureCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
        float delta = mMeasureCurrent - mMeasureLast;
        mMeasure = mMeasure * 0.9f + delta; // perform low-cut filter
        //Przekazywanie do MenuActivity odtąd
        array = event.values;
        useHandler(array);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }

    // Funkcja testowa, przekazująca zmienne do MenuActivity
    private void useHandler(float[] measurement){
        Message msg2 = mHandler.obtainMessage();
        Bundle mBundle = new Bundle();
        mBundle.putFloatArray(String.valueOf(whichSensor), measurement);
        msg2.setData(mBundle);
        mHandler.sendMessage(msg2);
    }
}
