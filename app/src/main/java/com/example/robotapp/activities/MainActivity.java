package com.example.robotapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.robotapp.R;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_ADDRESS = "device_address";
    private String address = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        address = intent.getStringExtra(EXTRA_ADDRESS);

        TextView textView = findViewById(R.id.textView);
        textView.setText(address);
    }
}
