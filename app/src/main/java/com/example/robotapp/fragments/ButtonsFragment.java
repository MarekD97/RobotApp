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

    public ButtonsFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_buttons, container, false);

        final SeekBar seekBar1 = (SeekBar) rootView.findViewById(R.id.seekBar1);
        final SeekBar seekBar2 = (SeekBar) rootView.findViewById(R.id.seekBar2);
        final SeekBar seekBar3 = (SeekBar) rootView.findViewById(R.id.seekBar3);
        final SeekBar seekBar4 = (SeekBar) rootView.findViewById(R.id.seekBar4);
        final SeekBar seekBar5 = (SeekBar) rootView.findViewById(R.id.seekBar5);

        seekBar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ((MainActivity) getActivity()).sendToBluetoothDevice("A0:" + progress + ";");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ((MainActivity) getActivity()).sendToBluetoothDevice("A1:" + progress + ";");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekBar3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ((MainActivity) getActivity()).sendToBluetoothDevice("A2:" + progress + ";");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekBar4.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ((MainActivity) getActivity()).sendToBluetoothDevice("A3:" + progress + ";");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekBar5.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ((MainActivity) getActivity()).sendToBluetoothDevice("A4:" + progress + ";");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        final Button buttonForward = (Button) rootView.findViewById(R.id.buttonUp);
        final Button buttonBackwards = (Button) rootView.findViewById(R.id.buttonDown);
        final Button buttonLeft = (Button) rootView.findViewById(R.id.buttonLeft);
        final Button buttonRight = (Button) rootView.findViewById(R.id.buttonRight);

        buttonForward.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        ((MainActivity) getActivity()).sendToBluetoothDevice("D0");
                        buttonForward.setBackgroundResource(R.drawable.ic_active_circle);
                        break;
                    case MotionEvent.ACTION_UP:
                        buttonForward.setBackgroundResource(R.drawable.ic_fill_circle);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
        buttonBackwards.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        ((MainActivity) getActivity()).sendToBluetoothDevice("D1");
                        buttonBackwards.setBackgroundResource(R.drawable.ic_active_circle);
                        break;
                    case MotionEvent.ACTION_UP:
                        buttonBackwards.setBackgroundResource(R.drawable.ic_fill_circle);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
        buttonLeft.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        ((MainActivity) getActivity()).sendToBluetoothDevice("D2");
                        buttonLeft.setBackgroundResource(R.drawable.ic_active_circle);
                        break;
                    case MotionEvent.ACTION_UP:
                        buttonLeft.setBackgroundResource(R.drawable.ic_fill_circle);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
        buttonRight.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        ((MainActivity) getActivity()).sendToBluetoothDevice("D3");
                        buttonRight.setBackgroundResource(R.drawable.ic_active_circle);
                        break;
                    case MotionEvent.ACTION_UP:
                        buttonRight.setBackgroundResource(R.drawable.ic_fill_circle);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        return rootView;
    }
}
