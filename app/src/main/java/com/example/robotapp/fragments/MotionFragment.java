package com.example.robotapp.fragments;


import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.robotapp.R;
import com.example.robotapp.activities.MainActivity;
import com.example.robotapp.services.SensorService;

import java.sql.Driver;

public class MotionFragment extends Fragment {

    private int selectedMode = 0;   //0 - arm, 1 - gripper, 2 - vehicle
    private boolean powerEnabled = false;

    private float[] position;
    private boolean driveTo = false;
    private float driveRotationX;
    private float driveRotationZ;

    private float gripperPositionX;
    private float gripperRotationX;

    ProgressBar progressBar2;
    ProgressBar progressBar3;


    public MotionFragment() {
        position = new float[4];
        driveRotationX = driveRotationZ = 0;
        gripperPositionX = gripperRotationX = 50;
        for(int i=0;i<4;i++) {
            position[i] = 50;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_motion, container, false);
        SensorService mSensorService = new SensorService(getContext(), sensorHandler); //Akcelerometr

        final Button buttonArm = rootView.findViewById(R.id.buttonArm);
        final Button buttonGripper = rootView.findViewById(R.id.buttonGripper);
        final Button buttonVehicle = rootView.findViewById(R.id.buttonVehicle);

        progressBar2 = rootView.findViewById(R.id.progressBar2);
        progressBar3 = rootView.findViewById(R.id.progressBar3);

        switch (selectedMode) {
            case 0:
                buttonArm.setBackgroundResource(R.drawable.ic_active_circle);
                buttonGripper.setBackgroundResource(R.drawable.ic_fill_circle);
                buttonVehicle.setBackgroundResource(R.drawable.ic_fill_circle);
                break;
            case 1:
                buttonArm.setBackgroundResource(R.drawable.ic_fill_circle);
                buttonGripper.setBackgroundResource(R.drawable.ic_active_circle);
                buttonVehicle.setBackgroundResource(R.drawable.ic_fill_circle);
                break;
            case 2:
                buttonArm.setBackgroundResource(R.drawable.ic_fill_circle);
                buttonGripper.setBackgroundResource(R.drawable.ic_fill_circle);
                buttonVehicle.setBackgroundResource(R.drawable.ic_active_circle);
                break;
            default:
                buttonArm.setBackgroundResource(R.drawable.ic_active_circle);
                buttonGripper.setBackgroundResource(R.drawable.ic_fill_circle);
                buttonVehicle.setBackgroundResource(R.drawable.ic_fill_circle);
                selectedMode = 0;
                break;
        }

        buttonArm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonArm.setBackgroundResource(R.drawable.ic_active_circle);
                buttonGripper.setBackgroundResource(R.drawable.ic_fill_circle);
                buttonVehicle.setBackgroundResource(R.drawable.ic_fill_circle);
                selectedMode = 0;
            }
        });
        buttonGripper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonArm.setBackgroundResource(R.drawable.ic_fill_circle);
                buttonGripper.setBackgroundResource(R.drawable.ic_active_circle);
                buttonVehicle.setBackgroundResource(R.drawable.ic_fill_circle);
                selectedMode = 1;
            }
        });
        buttonVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonArm.setBackgroundResource(R.drawable.ic_fill_circle);
                buttonGripper.setBackgroundResource(R.drawable.ic_fill_circle);
                buttonVehicle.setBackgroundResource(R.drawable.ic_active_circle);
                selectedMode = 2;
            }
        });

        final Button buttonPower = rootView.findViewById(R.id.buttonPower);
        buttonPower.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        powerEnabled = true;
                        buttonPower.setBackgroundResource(R.drawable.ic_active_circle);
                        break;
                    case MotionEvent.ACTION_UP:
                        powerEnabled = false;
                        buttonPower.setBackgroundResource(R.drawable.ic_fill_circle);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
        return rootView;
    }
    //Odczyt z SensorService
    @SuppressLint("HandlerLeak")
    private final Handler sensorHandler = new Handler(){
        public void handleMessage(Message msg2){
            Bundle bundle = msg2.getData();
            float[] measure = bundle.getFloatArray("Measurement");

            Log.i("Accelerometer", " :" + measure[0] + " " + measure[1] + " " + measure[2]);
            Log.i("Gyroscope", " :" + measure[3] + " " + measure[4] + " " + measure[5]);

            float posX = measure[0];
            float posY = measure[1];
            float posZ = measure[2];

            float rotX = measure[3];
            float rotY = measure[4];
            float rotZ = measure[5];

        if(powerEnabled) {
            switch (selectedMode) {
                case 0:


                    break;
                case 1:
                    int gripperDirection = DetectMotionGripper(posX, rotX);
                    switch (gripperDirection) {
                        case 0:
                            ((MainActivity) getContext()).sendToBluetoothDevice("A4:"+gripperRotationX+";");
                            break;
                        case 1:
                            ((MainActivity) getContext()).sendToBluetoothDevice("A3:"+gripperPositionX+";");
                            default: break;
                    }
                    Log.i("Gripper", " :"+gripperPositionX+" :"+gripperRotationX);
                    progressBar2.setProgress((int)gripperPositionX);
                    progressBar3.setProgress((int)gripperRotationX);
                    break;
                case 2:
                    driveTo = true;
                    int driveDirection = DetectMotionVehicle(rotX, rotZ);
                    switch (driveDirection) {
                        case 0:
                            ((MainActivity) getContext()).sendToBluetoothDevice("D0");
                            break;
                        case 1:
                            ((MainActivity) getContext()).sendToBluetoothDevice("D1");
                            break;
                        case 2:
                            ((MainActivity) getContext()).sendToBluetoothDevice("D2");
                            break;
                        case 3:
                            ((MainActivity) getContext()).sendToBluetoothDevice("D3");
                            break;
                            default: break;
                    }
                    Log.i("Vehicle", " :"+driveRotationX+" :"+driveRotationZ);
                    break;
                default:
                    break;
            }
        } else {
            driveTo = false;
            driveRotationX = driveRotationZ = 0;
        }
        }
    };

    private int DetectMotionVehicle(float rotationX, float rotationZ) {
        if(Math.abs(rotationX) > 1) {
            driveRotationX += rotationX;
            driveRotationZ = 0;
            if(rotationX>5)
                driveRotationX = 5;
            else if(rotationX<-5)
                driveRotationX = -5;
        } else if(Math.abs(rotationZ)>1){
            driveRotationX = 0;
            driveRotationZ += rotationZ;
            if(rotationZ>5)
                driveRotationZ = 5;
            else if(rotationZ < -5)
                driveRotationZ = -5;
        }
        if(driveRotationX < -4)
            return 0;
        else if(driveRotationX > 4)
            return 1;
        else if(driveRotationZ > 4)
            return 2;
        else if(driveRotationZ < -4)
            return 3;
        return -1;
    }

    private int DetectMotionGripper(float positionX, float rotationX) {
        if(Math.abs(positionX) > .1 ) {
            gripperPositionX += positionX*10;
            if(gripperPositionX < 0)
                gripperPositionX = 0;
            else if (gripperPositionX > 100)
                gripperPositionX = 100;
            return 0;
        } else if(Math.abs(rotationX) > .1) {
            gripperRotationX += rotationX*10;
            if(gripperRotationX < 0)
                gripperRotationX = 0;
            else if(gripperRotationX > 100)
                gripperRotationX = 100;
            return 1;
        }
        return  -1;
    }

    private int DetectMotionArm(float positionX, float positionZ, float rotationZ) {
        return 0;
    }
}
