package com.example.robotapp.services;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.UUID;

public class BluetoothService {
    private static final UUID mUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final String mName = "RobotApp";

    public Handler handler; // handler that gets info from Bluetooth service
    private BluetoothAdapter bluetoothAdapter;

    private AcceptThread acceptThread;
    private ConnectThread connectThread;
    private ConnectedThread connectedThread;
    private int state;

    // Constants that indicate the current connection state
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device

    public BluetoothService(Context context, Handler handler) {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        state = STATE_NONE;
        this.handler = handler;
    }

    // Defines several constants used when transmitting messages between the
    // service and the UI.
    public interface MessageConstants {
        int MESSAGE_READ = 0;
        int MESSAGE_WRITE = 1;
        int MESSAGE_TOAST = 2;
        int MESSAGE_STATE_CHANGE = 3;
        int MESSAGE_DEVICE_NAME = 4;

        // Key names received from the BluetoothChatService Handler
        String DEVICE_NAME = "device_name";
        String TOAST = "toast";
    }

    public synchronized int getState() {
        return state;
    }

    public synchronized void start() {
        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }

        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }

        if (acceptThread == null) {
            acceptThread = new AcceptThread();
            acceptThread.start();
        }
    }

    public synchronized void connect(BluetoothDevice bluetoothDevice) {
        if (state == STATE_CONNECTING) {
            if (connectThread != null) {
                connectThread.cancel();
                connectThread = null;
            }
        }

        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }

        connectThread = new ConnectThread(bluetoothDevice);
        connectThread.start();

        Message msg = handler.obtainMessage(MessageConstants.MESSAGE_STATE_CHANGE, STATE_CONNECTING, -1);
        msg.sendToTarget();
    }

    public synchronized void connected(BluetoothSocket bluetoothSocket, BluetoothDevice device) {
        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }

        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }

        if (acceptThread != null) {
            acceptThread.cancel();
            acceptThread = null;
        }
        connectedThread = new ConnectedThread(bluetoothSocket);
        connectedThread.start();

        Message msg = handler.obtainMessage(MessageConstants.MESSAGE_DEVICE_NAME);
        Bundle bundle = new Bundle();
        bundle.putString(MessageConstants.DEVICE_NAME, device.getName());
        msg.setData(bundle);
        handler.sendMessage(msg);
        msg = handler.obtainMessage(MessageConstants.MESSAGE_STATE_CHANGE, STATE_CONNECTED, -1);
        msg.sendToTarget();
    }

    public synchronized void stop() {
        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }

        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }

        if (acceptThread != null) {
            acceptThread.cancel();
            acceptThread = null;
        }
        state = STATE_NONE;

        Message msg = handler.obtainMessage(MessageConstants.MESSAGE_STATE_CHANGE, STATE_NONE, -1);
        msg.sendToTarget();
    }

    public void send(byte[] out) {
        ConnectedThread tempConnected;
        synchronized (this) {
            if (connectedThread == null) return;
            tempConnected = connectedThread;
        }
        tempConnected.write(out);
    }

    private void connectionFailed() {
        Message msg = handler.obtainMessage(MessageConstants.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(MessageConstants.TOAST, "Unable to connect device");
        msg.setData(bundle);
        handler.sendMessage(msg);
        state = STATE_NONE;
        BluetoothService.this.start();
    }

    private void connectionLost() {
        Message msg = handler.obtainMessage(MessageConstants.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(MessageConstants.TOAST, "Device connection was lost");
        msg.setData(bundle);
        handler.sendMessage(msg);
        state = STATE_NONE;

        BluetoothService.this.start();
    }

    private class AcceptThread extends Thread {
        //the local server socket
        private final BluetoothServerSocket serverSocket;

        public AcceptThread() {
            BluetoothServerSocket tempSocket = null;
            try {
                tempSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(mName, mUUID);
            } catch (IOException e) {
            }
            serverSocket = tempSocket;
            state = STATE_LISTEN;

        }

        public void run() {
            BluetoothSocket socket = null;

            while (state != STATE_CONNECTED) {
                try {
                    socket = serverSocket.accept();
                } catch (IOException e) {
                }
                if (socket != null) {
                    synchronized (BluetoothService.this) {
                        switch (state) {
                            case STATE_LISTEN:
                            case STATE_CONNECTING:
                                // Situation normal. Start the connected thread.
                                connected(socket, socket.getRemoteDevice());
                                break;
                            case STATE_NONE:
                            case STATE_CONNECTED:
                                // Either not ready or already connected. Terminate new socket.
                                try {
                                    socket.close();
                                } catch (IOException e) {
                                }
                                break;
                        }
                    }
                }
            }
        }

        public void cancel() {
            try {
                serverSocket.close();
            } catch (IOException e) {
            }
        }
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket bluetoothSocket;
        private final BluetoothDevice bluetoothDevice;

        public ConnectThread(BluetoothDevice device) {
            BluetoothSocket tempSocket = null;
            bluetoothDevice = device;

            try {
                tempSocket = device.createRfcommSocketToServiceRecord(mUUID);
            } catch (IOException e) {
            }
            bluetoothSocket = tempSocket;
            state = STATE_CONNECTING;
        }

        public void run() {
            bluetoothAdapter.cancelDiscovery();

            try {
                bluetoothSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and return.
                try {
                    bluetoothSocket.close();
                } catch (IOException closeException) {
                }
                connectionFailed();
                return;
            }
            synchronized (BluetoothService.this) {
                connectThread = null;
            }
            connected(bluetoothSocket, bluetoothSocket.getRemoteDevice());
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
                tempOut = socket.getOutputStream();
            } catch (IOException e) {
            }

            inputStream = tempIn;
            outputStream = tempOut;
            state = STATE_CONNECTED;
        }

        public void run() {
            buffer = new byte[16];
            int numBytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs.
            while (state == STATE_CONNECTED) {
                try {
                    // Read from the InputStream.
                    numBytes = inputStream.read(buffer);
                    Message readMsg = handler.obtainMessage(
                            MessageConstants.MESSAGE_READ, numBytes, -1,
                            buffer);
                    readMsg.sendToTarget();
                } catch (IOException e) {
                    connectionLost();
                    break;
                }
            }
        }

        // Call this from the main activity to send data to the remote device.
        public void write(byte[] bytes) {
            try {
                outputStream.write(bytes);

            } catch (Exception e) {
            }
        }

        public void cancel() {
            try {
                bluetoothSocket.close();
            } catch (IOException e) {
            }
        }
    }
}
