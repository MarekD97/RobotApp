package com.example.robotapp.activities;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.robotapp.R;
import com.example.robotapp.services.SensorService; //-------------------

import java.util.ArrayList;
import java.util.Set;

public class MenuActivity extends AppCompatActivity {

    private BluetoothAdapter bluetoothAdapter;
    private static final int REQUEST_ENABLE_BT = 1001;
    ArrayList<String> arrayPairedDevices;
    ArrayList<String> arrayFoundDevices;

    private SensorService mSensorService;

    ListView listViewAvailableDevice;
    ListView listViewConnectDevice;
    public static final String EXTRA_ADDRESS = "device_address";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        listViewConnectDevice = findViewById(R.id.listViewConnectDevice);
        listViewAvailableDevice = findViewById(R.id.listViewAvailableDevice);

        AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                bluetoothAdapter.cancelDiscovery();
                String info = ((TextView)view).getText().toString();
                String address = info.substring(info.length() - 17);
                Intent intent = new Intent(MenuActivity.this, MainActivity.class);
                intent.putExtra(EXTRA_ADDRESS, address);
                startActivity(intent);
            }
        };
        listViewAvailableDevice.setOnItemClickListener(onItemClickListener);
        listViewConnectDevice.setOnItemClickListener(onItemClickListener);

        mSensorService = new SensorService(this, mHandler); //Akcelerometr

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null) {
            if (!bluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
            else {
                findDevice();
            }
        }
        else {
            Toast.makeText(this, "Your device does not support Bluetooth!", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void findDevice() {
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            arrayPairedDevices = new ArrayList();

            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress();

                arrayPairedDevices.add(deviceName+"\r\n"+deviceHardwareAddress);
            }
            ArrayAdapter<String> adapter = new ArrayAdapter(MenuActivity.this, R.layout.fragment_list, arrayPairedDevices);
            listViewConnectDevice.setAdapter(adapter);
        }
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);
        if(bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
        bluetoothAdapter.startDiscovery();
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address

                arrayFoundDevices = new ArrayList<String>();
                arrayFoundDevices.add(deviceName+"\r\n"+deviceHardwareAddress);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(MenuActivity.this, R.layout.fragment_list, arrayFoundDevices);
                listViewAvailableDevice.setAdapter(adapter);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(receiver);
    }

    public void onClickButton(View view) {
        findDevice();
    }


    //Odczyt z handlera tutaj
    private final Handler mHandler = new Handler(){
        public void handleMessage(Message msg2){

            byte[] measure = (byte[]) msg2.obj;
            //String measure = (String) msg2.obj;
            //Log.i("Proba2", String.valueOf(measure));
        }
    };


}
