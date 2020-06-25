package com.example.robotapp.services;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class SensorService implements SensorEventListener{
    private final SensorManager mSensorManager;
    private final Sensor mAccelerometer, mGravity, mGyroscope;
    public Handler mHandler;

    private float mMeasure; // acceleration apart from gravity
    private float mMeasureCurrent; // current acceleration including gravity
    private float mMeasureLast; // last acceleration including gravity
    private float[] array = new float[9];

    public SensorService(Context context, Handler handler) {
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mGravity = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mGravity, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mGyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        this.mHandler = handler;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        if(sensor.getType() == Sensor.TYPE_GRAVITY){
            array[6] = event.values[0];
            array[7] = event.values[1];
            array[8] = event.values[2];
            Log.i("Grawitacja", " "+array[6]+" "+array[7]+" "+array[8]);
        }else if (sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            array[0] = event.values[0] - array[6];
            array[1] = event.values[1] - array[7];
            array[2] = event.values[2] - array[8];
            Log.i("Akcelerometr", " "+array[0]+" "+array[1]+" "+array[2]);
        }else if (sensor.getType() == Sensor.TYPE_GYROSCOPE){
            array[3] = event.values[0];
            array[4] = event.values[1];
            array[5] = event.values[2];
            Log.i("Zyroskop", " "+array[3]+" "+array[4]+" "+array[5]);
        }
        /*
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        mMeasureLast = mMeasureCurrent;
        mMeasureCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
        float delta = mMeasureCurrent - mMeasureLast;
        mMeasure = mMeasure * 0.9f + delta; // perform low-cut filter
        //Przekazywanie do MenuActivity odtąd
        array = event.values;*/
        useHandler(array);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }

    // Funkcja testowa, przekazująca zmienne do MenuActivity
    private void useHandler(float[] measurement){
        Message msg2 = mHandler.obtainMessage();
        Bundle mBundle = new Bundle();
        mBundle.putFloatArray("Measurement", measurement);
        msg2.setData(mBundle);
        mHandler.sendMessage(msg2);
    }

}
