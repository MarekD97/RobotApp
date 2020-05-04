package com.example.robotapp.activities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.robotapp.R;

import java.util.ArrayList;
import java.util.Set;

public class MenuActivity extends AppCompatActivity {

    private BluetoothAdapter bluetoothAdapter;
    private static final int REQUEST_ENABLE_BT = 1001;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set Content View from xml resources
        setContentView(R.layout.activity_menu);

        //get default Bluetooth adapter
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //check if the device supports Bluetooth
        if (bluetoothAdapter != null) {
            //check if the Bluetooth is enabled
            if (!bluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
            else {
                findDevice();
                discoverable();
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
            ArrayList<String> arrayDevices = new ArrayList<String>();
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                arrayDevices.add(deviceName);

            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(MenuActivity.this, R.layout.fragment_list, arrayDevices);
            listView = findViewById(R.id.listViewConnectDevice);
            listView.setAdapter(adapter);
        }
        bluetoothAdapter.startDiscovery();
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address

                ArrayList<String> arrayDevice = new ArrayList<String>();
                arrayDevice.add(deviceName);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(MenuActivity.this, R.layout.fragment_list, arrayDevice);
                listView = findViewById(R.id.listViewAvailableDevice);
                listView.setAdapter(adapter);
            }
        }
    };

    private void discoverable() {
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(bluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
        startActivity(discoverableIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(receiver);
    }
}
