package com.example.robotapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.hardware.Sensor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.robotapp.R;
import com.example.robotapp.services.BluetoothService;
import com.example.robotapp.services.SensorService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_ADDRESS = "device_address";
    private String address = null;
    private BluetoothService bluetoothService;
    BluetoothDevice bluetoothDevice;
    private byte[] buffer;
    private SensorService accelSensorService;
    private SensorService gyroSensorService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        address = intent.getStringExtra(EXTRA_ADDRESS);

        final TextView textView = findViewById(R.id.textView);
        textView.setText(address);
        bluetoothDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address);
        bluetoothService = new BluetoothService(this, handler);
        bluetoothService.connect(bluetoothDevice);

        accelSensorService = new SensorService(this, aHandler, Sensor.TYPE_ACCELEROMETER); //Akcelerometr
        gyroSensorService = new SensorService(this, gHandler, Sensor.TYPE_GYROSCOPE); //Żyroskop

        SeekBar seekBar = findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textView.setText(String.valueOf(progress));
                bluetoothService.send((progress + "\n").getBytes());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bluetoothService.stop();
    }

    public void onClickSend(View view) {
        EditText textInput = findViewById(R.id.editText);
        String inputString = textInput.getText().toString();

        bluetoothService.send(inputString.getBytes());
    }

    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BluetoothService.MessageConstants.MESSAGE_STATE_CHANGE:
                    ProgressBar progressBar = findViewById(R.id.progressBar);
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:
                            Toast.makeText(MainActivity.this, "Connected", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                            break;
                        case BluetoothService.STATE_CONNECTING:
                            Toast.makeText(MainActivity.this, "Connecting...", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.VISIBLE);
                            break;
                        case BluetoothService.STATE_LISTEN:
                        case BluetoothService.STATE_NONE:
                            Toast.makeText(MainActivity.this, bluetoothDevice.getName()+" is disconnected", Toast.LENGTH_LONG).show();
                            break;
                    }
                    break;
                case BluetoothService.MessageConstants.MESSAGE_WRITE:
                    byte[] buffer = (byte[]) msg.obj;
                    TextView textView = findViewById(R.id.textView);
                    textView.setText(buffer.toString());
                    break;
                case BluetoothService.MessageConstants.MESSAGE_READ:
                case BluetoothService.MessageConstants.MESSAGE_DEVICE_NAME:
                    Toast.makeText(MainActivity.this, "Connected to " + bluetoothDevice.getName(), Toast.LENGTH_LONG).show();
                    break;
                case BluetoothService.MessageConstants.MESSAGE_TOAST:
                    Toast.makeText(MainActivity.this, msg.getData().getString(BluetoothService.MessageConstants.TOAST), Toast.LENGTH_LONG).show();
                    finish();
                    break;
            }
        }
    };

    //Odczyt z handlera tutaj
    private final Handler aHandler = new Handler(){
        public void handleMessage(Message msg2){
            Bundle bundle = msg2.getData();
            float[] aMeasure = bundle.getFloatArray(String.valueOf(Sensor.TYPE_ACCELEROMETER));
            Log.i("Akcelerometr", "x: "+aMeasure[0]+"; y: "+aMeasure[1]+"; z: "+aMeasure[2]);
        }
    };

    private final Handler gHandler = new Handler(){
        public void handleMessage(Message msg3){
            Bundle bundle = msg3.getData();
            float[] gMeasure = bundle.getFloatArray(String.valueOf(Sensor.TYPE_GYROSCOPE));
            Log.i("Zyroskop", "x: "+gMeasure[0]+"; y: "+gMeasure[1]+"; z: "+gMeasure[2]);
        }
    };

}
