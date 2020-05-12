package com.example.robotapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.robotapp.fragments.MotionFragment;
import com.example.robotapp.R;
import com.example.robotapp.fragments.SettingsFragment;
import com.example.robotapp.fragments.ButtonsFragment;
import com.example.robotapp.services.BluetoothService;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends AppCompatActivity implements ButtonsFragment.OnFragmentInteractionListener {

    public static final String EXTRA_ADDRESS = "device_address";
    private BluetoothService bluetoothService;
    BluetoothDevice bluetoothDevice;
    private byte[] buffer;

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
//        final TextView textView = findViewById(R.id.textView);
//        textView.setText(address);
        bluetoothDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address);
        bluetoothService = new BluetoothService(this, handler);
        bluetoothService.connect(bluetoothDevice);

//        SeekBar seekBar = findViewById(R.id.seekBar);
//        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                textView.setText(String.valueOf(progress));
//                bluetoothService.send((progress + "\n").getBytes());
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        });
    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
    }

    @Override
    public void onArticleSelected(int position) {
        viewPager.setCurrentItem(position);
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
    public void onAttachFragment(Fragment fragment) {
        if (fragment instanceof ButtonsFragment) {
            ButtonsFragment headlinesFragment = (ButtonsFragment) fragment;
            headlinesFragment.setOnFragmentSelectedListener(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bluetoothService.stop();
    }

    public void onClickSend(View view) {
//        EditText textInput = findViewById(R.id.editText);
//        String inputString = textInput.getText().toString();

//        bluetoothService.send(inputString.getBytes());
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
                            Toast.makeText(MainActivity.this, bluetoothDevice.getName() + " is disconnected", Toast.LENGTH_LONG).show();
                            break;
                    }
                    break;
                case BluetoothService.MessageConstants.MESSAGE_WRITE:
                    byte[] buffer = (byte[]) msg.obj;
//                    TextView textView = findViewById(R.id.textView);
//                    textView.setText(buffer.toString());
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
}
