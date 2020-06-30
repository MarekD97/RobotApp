package com.example.robotapp.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.robotapp.R;
import com.example.robotapp.activities.MainActivity;

public class ButtonsFragment extends Fragment {

    SeekBar seekBar[] = new SeekBar[6];

    Button button[] = new Button[4];

    public ButtonsFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_buttons, container, false);

        seekBar[0] = (SeekBar) rootView.findViewById(R.id.seekBar1);
        seekBar[1] = (SeekBar) rootView.findViewById(R.id.seekBar2);
        seekBar[2] = (SeekBar) rootView.findViewById(R.id.seekBar3);
        seekBar[3] = (SeekBar) rootView.findViewById(R.id.seekBar4);
        seekBar[4] = (SeekBar) rootView.findViewById(R.id.seekBar5);
        seekBar[5] = (SeekBar) rootView.findViewById(R.id.seekBar6);

        for(int i = 0; i <6; i++) {
            seekBar[i].setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    ((MainActivity) getActivity()).sendToBluetoothDevice("A"+seekBar.getTag()+":" + progress + ";");
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        }

        button[0] = (Button) rootView.findViewById(R.id.buttonUp);
        button[1] = (Button) rootView.findViewById(R.id.buttonDown);
        button[2] = (Button) rootView.findViewById(R.id.buttonLeft);
        button[3] = (Button) rootView.findViewById(R.id.buttonRight);

       for(int i = 0; i<4;i++) {
           button[i].setOnTouchListener(new View.OnTouchListener() {
               @Override
               public boolean onTouch(View v, MotionEvent event) {
                   ((MainActivity) getActivity()).sendToBluetoothDevice("D"+v.getTag());
                   switch (event.getAction()) {
                       case MotionEvent.ACTION_DOWN:
                           v.setBackgroundResource(R.drawable.ic_active_circle);
                           break;
                       case MotionEvent.ACTION_UP:
                           v.setBackgroundResource(R.drawable.ic_fill_circle);
                           break;
                       default:
                           break;
                   }
                   return true;
               }
           });
       }

        return rootView;
    }

    public void setSeekBarsProgress() {
        seekBar[0].setProgress((int)((MainActivity) getActivity()).armPosition[0]);
        seekBar[1].setProgress((int)((MainActivity) getActivity()).armPosition[1]);
        seekBar[2].setProgress((int)((MainActivity) getActivity()).armPosition[2]);
        seekBar[3].setProgress((int)((MainActivity) getActivity()).armPosition[3]);
        seekBar[4].setProgress((int)((MainActivity) getActivity()).armPosition[4]);
        seekBar[5].setProgress((int)((MainActivity) getActivity()).armPosition[5]);
    }
}
