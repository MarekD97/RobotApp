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

    TextView textView;
    TextView textViewWarning;

    private int selectedMode = 0;   //0 - arm, 1 - gripper, 2 - vehicle
    private boolean powerEnabled = false;

    ProgressBar progressBar2;
    ProgressBar progressBar3;
    ProgressBar progressBar4;

    Button[] buttons = new Button[3];


    public MotionFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_motion, container, false);
        SensorService mSensorService = new SensorService(getContext(), sensorHandler); //Akcelerometr

        buttons[0] = rootView.findViewById(R.id.buttonArm);
        buttons[1] = rootView.findViewById(R.id.buttonGripper);
        buttons[2] = rootView.findViewById(R.id.buttonVehicle);

        textView = rootView.findViewById(R.id.textViewSensor);
        textViewWarning = rootView.findViewById(R.id.textViewWarning);

        progressBar2 = rootView.findViewById(R.id.progressBar2);
        progressBar3 = rootView.findViewById(R.id.progressBar3);
        progressBar4 = rootView.findViewById(R.id.progressBar4);

        for(int i=0; i<3;i++) {
            if(i==selectedMode)
                buttons[i].setBackgroundResource(R.drawable.ic_active_circle);
            else
                buttons[i].setBackgroundResource(R.drawable.ic_fill_circle);
            buttons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedMode = Integer.valueOf(v.getTag().toString());
                    for(int i=0; i<3;i++) {
                        if (i == selectedMode)
                            buttons[i].setBackgroundResource(R.drawable.ic_active_circle);
                        else
                            buttons[i].setBackgroundResource(R.drawable.ic_fill_circle);
                    }
                }
            });
        }

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

            float posX = measure[0];
            float posY = measure[1];
            float posZ = measure[2];

            textView.setText("Pomiary:\n"+posX+",\n"+posY+",\n"+posZ);

        if(powerEnabled) {
            switch (selectedMode) {
                case 0:
                    int armRx = (int)Math.min(180, Math.max(0, -posX*10 +50));
                    int armRy = (int)Math.min(180, Math.max(0, -posY*10 +50));

                    if(posZ>7) {
                        ((MainActivity) getContext()).sendToBluetoothDevice("A0:" + armRx + ";");
                        progressBar2.setProgress(armRx);
                        ((MainActivity) getContext()).sendToBluetoothDevice("A1:"+armRy+";");
                        progressBar3.setProgress(armRy);
                    }
                    else {
                        ((MainActivity) getContext()).sendToBluetoothDevice("A2:" + armRx + ";");
                        progressBar4.setProgress(armRy);
                    }

                    break;
                case 1:
                    int gripperRx = (int)Math.min(180, Math.max(0, -posX*10 +50));
                    int gripperRy = (int)Math.min(180, Math.max(0, -posY*10 +50));

                    if(posZ>7) {
                        ((MainActivity) getContext()).sendToBluetoothDevice("A4:" + gripperRx + ";");
                        progressBar2.setProgress(gripperRx);
                        ((MainActivity) getContext()).sendToBluetoothDevice("A3:"+gripperRy+";");
                        progressBar3.setProgress(gripperRy);
                    }
                    else {
                        ((MainActivity) getContext()).sendToBluetoothDevice("A5:" + gripperRx + ";");
                        progressBar4.setProgress(gripperRx);
                    }
                    break;
                case 2:
                    int driveDirection = DetectMotionVehicle(posX, posY);
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
                    break;
                default:
                    break;
            }
        }
        }
    };

    private int DetectMotionVehicle(float positionX, float positionY) {
        if(positionY < -4)
            return 0;
        else if(positionY > 4)
            return 1;
        else if(positionX > 4)
            return 2;
        else if(positionX < -4)
            return 3;
        return -1;
    }

    public void setActiveButtons(boolean enabled) {
        buttons[0].setEnabled(enabled);
        buttons[1].setEnabled(enabled);
        if(!enabled) {
            textViewWarning.setVisibility(View.VISIBLE);
            buttons[0].setBackgroundResource(R.drawable.ic_fill_circle);
            buttons[1].setBackgroundResource(R.drawable.ic_fill_circle);
            buttons[2].setBackgroundResource(R.drawable.ic_active_circle);
        } else {
            textViewWarning.setVisibility(View.GONE);
            buttons[0].setBackgroundResource(R.drawable.ic_active_circle);
            buttons[1].setBackgroundResource(R.drawable.ic_fill_circle);
            buttons[2].setBackgroundResource(R.drawable.ic_fill_circle);
        }

    }
}
