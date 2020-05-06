package com.example.robotapp.services;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.os.Message;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class BluetoothService {
    static final UUID mUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    static final String NAME = "robotapp";
    public Handler handler; // handler that gets info from Bluetooth service
    private BluetoothAdapter bluetoothAdapter;
    private ConnectThread connectThread;
    private ConnectedThread connectedThread;

    public BluetoothService(Context context, Handler handler) {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.handler = handler;
    }

    // Defines several constants used when transmitting messages between the
    // service and the UI.
    private interface MessageConstants {
        public static final int MESSAGE_READ = 0;
        public static final int MESSAGE_WRITE = 1;
        public static final int MESSAGE_TOAST = 2;

        // ... (Add other message types here as needed.)
    }

    public synchronized void connect(BluetoothDevice bluetoothDevice) {
        if(connectThread!=null) {
            connectThread.cancel();
            connectThread = null;
        }

        if(connectedThread!=null) {
            connectedThread.cancel();
            connectedThread = null;
        }

        connectThread = new ConnectThread(bluetoothDevice);
        connectThread.start();
    }

    public synchronized void connected(BluetoothSocket bluetoothSocket) {
//        if(connectThread!=null) {
//            connectThread.cancel();
//            connectThread = null;
//        }

        if(connectedThread!=null) {
            connectedThread.cancel();
            connectedThread = null;
        }
        connectedThread = new ConnectedThread(bluetoothSocket);
        connectedThread.start();
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket bluetoothSocket;
        private final BluetoothDevice bluetoothDevice;

        public ConnectThread(BluetoothDevice device) {
            BluetoothSocket tempSocket = null;
            bluetoothDevice = device;

            try {
                tempSocket = device.createRfcommSocketToServiceRecord(mUUID);
            } catch (IOException e) {}
            bluetoothSocket = tempSocket;
        }

        public void run() {
            bluetoothAdapter.cancelDiscovery();

            try {
                bluetoothSocket.connect();
                System.out.println("Connected");
            } catch (IOException connectException) {
                // Unable to connect; close the socket and return.
                try {
                    bluetoothSocket.close();
                } catch (IOException closeException) {}
                return;
            }
            connected(bluetoothSocket);
        }

        public void cancel() {
            try {
                bluetoothSocket.close();
            } catch (IOException e) {
            }
        }
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket bluetoothSocket;
        private final InputStream inputStream;
        private final OutputStream outputStream;
        private byte[] buffer; // mmBuffer store for the stream

        public ConnectedThread(BluetoothSocket socket) {
            bluetoothSocket = socket;
            InputStream tempIn = null;
            OutputStream tempOut = null;

            try {
                tempIn = socket.getInputStream();
            } catch (IOException e) {}
            try {
                tempOut = socket.getOutputStream();
            } catch (IOException e) {}

            inputStream = tempIn;
            outputStream = tempOut;
        }

        public void run() {
            buffer = new byte[1024];
            int numBytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs.
            while (true) {
                try {
                    // Read from the InputStream.
                    numBytes = inputStream.read(buffer);
                    Message readMsg = handler.obtainMessage(
                            MessageConstants.MESSAGE_READ, numBytes, -1,
                            buffer);
                    readMsg.sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }

        // Call this from the main activity to send data to the remote device.
        public void write(byte[] bytes) {
            try {
                outputStream.write(bytes);

            } catch (IOException e) {}
        }

        public void cancel() {
            try {
                bluetoothSocket.close();
            } catch (IOException e) {}
        }
    }
}
