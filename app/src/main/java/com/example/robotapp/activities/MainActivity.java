package com.example.robotapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.robotapp.R;
import com.example.robotapp.services.BluetoothService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_ADDRESS = "device_address";
    private String address = null;
    private BluetoothService bluetoothService;
    private byte[] buffer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        address = intent.getStringExtra(EXTRA_ADDRESS);

        TextView textView = findViewById(R.id.textView);
        textView.setText(address);

        bluetoothService = new BluetoothService(this, handler);
        bluetoothService.connect(BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address));
    }

    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            buffer = (byte[]) message.obj;
            String readMessage = new String(buffer, 0, message.arg1 - 1);
            Toast.makeText(MainActivity.this, readMessage, Toast.LENGTH_LONG).show();
        }
    };
}
