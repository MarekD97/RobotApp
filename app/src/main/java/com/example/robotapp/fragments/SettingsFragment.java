package com.example.robotapp.fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.example.robotapp.R;
import com.example.robotapp.activities.MainActivity;

public class SettingsFragment extends Fragment {

    Button buttonStop;
    Switch switchMode;

    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        buttonStop = rootView.findViewById(R.id.buttonStop);
        buttonStop.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        ((MainActivity)getActivity()).sendToBluetoothDevice("STOP");
                        buttonStop.setBackgroundResource(R.drawable.ic_active_circle);
                        break;
                    case MotionEvent.ACTION_UP:
                        buttonStop.setBackgroundResource(R.drawable.ic_fill_circle);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
        switchMode = rootView.findViewById(R.id.switchMode);
        switchMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    ((MainActivity)getActivity()).sendToBluetoothDevice("M"+1);
                else
                    ((MainActivity)getActivity()).sendToBluetoothDevice("M"+0);
                ((MainActivity)getActivity()).setMode(!isChecked);
            }
        });
        return rootView;
    }

}
