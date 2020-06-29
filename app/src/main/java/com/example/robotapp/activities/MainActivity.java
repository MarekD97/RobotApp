package com.example.robotapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.robotapp.fragments.MotionFragment;
import com.example.robotapp.R;
import com.example.robotapp.fragments.SettingsFragment;
import com.example.robotapp.fragments.ButtonsFragment;
import com.example.robotapp.services.BluetoothService;
import com.example.robotapp.services.SensorService;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_ADDRESS = "device_address";
    private BluetoothService bluetoothService;
    BluetoothDevice bluetoothDevice;

    private static final int numPages = 3;
    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.pager);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        pagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        bottomNavigationView.setSelectedItemId(R.id.page_buttons);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                        switch (menuItem.getItemId()) {
                            case R.id.page_buttons:
                                viewPager.setCurrentItem(0);
                                break;
                            case R.id.page_motion:
                                viewPager.setCurrentItem(1);
                                break;
                            case R.id.page_settings:
                                viewPager.setCurrentItem(2);
                                break;
                        }
                        return false;
                    }
                }
        );

        String address = getIntent().getStringExtra(EXTRA_ADDRESS);
        bluetoothDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address);
        bluetoothService = new BluetoothService(this, handler);
        bluetoothService.connect(bluetoothDevice);
    }


    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
    }

    private class MainPagerAdapter extends FragmentStatePagerAdapter {
        public MainPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
//                    bottomNavigationView.setSelectedItemId(R.id.page_buttons);
                    return new ButtonsFragment();
                case 1:
//                    bottomNavigationView.setSelectedItemId(R.id.page_motion);
                    return new MotionFragment();
                default:
//                    bottomNavigationView.setSelectedItemId(R.id.page_settings);
                    return new SettingsFragment();
            }
        }

        public int getCount() {
            return numPages;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bluetoothService.stop();
    }

    public void sendToBluetoothDevice(String message) {
        bluetoothService.send(message.getBytes());
    }

    //Odczyt z BluetoothService
    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BluetoothService.MessageConstants.MESSAGE_STATE_CHANGE:
                    ConstraintLayout progressBarBackground = findViewById(R.id.progressBarBackground);
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:
                            Toast.makeText(MainActivity.this, "Połączono", Toast.LENGTH_LONG).show();
                            progressBarBackground.setVisibility(View.GONE);
                            break;
                        case BluetoothService.STATE_CONNECTING:
                            Toast.makeText(MainActivity.this, "Łączę...", Toast.LENGTH_LONG).show();
                            progressBarBackground.setVisibility(View.VISIBLE);
                            break;
                        case BluetoothService.STATE_LISTEN:
                        case BluetoothService.STATE_NONE:
                            Toast.makeText(MainActivity.this, bluetoothDevice.getName() + " został rozłączony", Toast.LENGTH_LONG).show();
                            break;
                    }
                    break;
                case BluetoothService.MessageConstants.MESSAGE_READ:
                    byte[] buffer = (byte[]) msg.obj;
                        TextView textViewCurrent = findViewById(R.id.textViewCurrentValue);
                        if(textViewCurrent!=null) {
                            String value = new String(buffer);
                            value = value.substring(value.indexOf("C")+1);
                            value = value.substring(0, value.indexOf(";"));
                            textViewCurrent.setText(value  + " A");
                        }
                    break;
                case BluetoothService.MessageConstants.MESSAGE_DEVICE_NAME:
                    Toast.makeText(MainActivity.this, "Połączono z " + bluetoothDevice.getName(), Toast.LENGTH_LONG).show();
                    break;
                case BluetoothService.MessageConstants.MESSAGE_TOAST:
                    Toast.makeText(MainActivity.this, msg.getData().getString(BluetoothService.MessageConstants.TOAST), Toast.LENGTH_LONG).show();
                    finish();
                    break;
            }
        }
    };

}
