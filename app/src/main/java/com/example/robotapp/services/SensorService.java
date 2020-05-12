package com.example.robotapp.services;

import android.app.Activity;
import android.app.Service;

import android.content.Context;

import android.location.LocationManager;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import android.os.Bundle;
import android.os.IBinder;
import android.os.Handler;

import android.os.Message;
import android.util.Log;


public class SensorService implements SensorEventListener{
    private final SensorManager mSensorManager;
    private final Sensor mAccelerometer;
    public Handler mHandler;

    private float mAccel; // acceleration apart from gravity
    private float mAccelCurrent; // current acceleration including gravity
    private float mAccelLast; // last acceleration including gravity
    private float[] array = new float[3];

    public SensorService(Context context, Handler handler) {
        //activity =
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        this.mHandler = handler;
    }



    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        mAccelLast = mAccelCurrent;
        mAccelCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
        float delta = mAccelCurrent - mAccelLast;
        mAccel = mAccel * 0.9f + delta; // perform low-cut filter
        //Przekazywanie do MenuActivity odtąd
        array = event.values;
        useHandler(array);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    // Funkcja testowa, przekazująca zmienne do MenuActivity
    private void useHandler(float[] measurement){
        Message msg2 = mHandler.obtainMessage();
        Bundle mBundle = new Bundle();
        mBundle.putFloatArray("measurement", measurement);
        msg2.setData(mBundle);
        mHandler.sendMessage(msg2);
    }
}
