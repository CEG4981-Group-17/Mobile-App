package com.example.icmapp.ui.home;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.icmapp.R;

public class HomeFragment extends Fragment {
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_DISCOVERY_BT = 2;

    private BluetoothAdapter BA;
    private HomeViewModel homeViewModel;
    private ToggleButton lockToggleButton;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        // gets refs to elements in home view
        BA = BluetoothAdapter.getDefaultAdapter();
        lockToggleButton = (ToggleButton) root.findViewById(R.id.lockToggleButton);

        // set callbacks
        lockToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    bluetoothSendLock();
                } else {
                    bluetoothSendUnlock();
                }
                // update button with actual locked status, in case it failed
                buttonView.setChecked(homeViewModel.isLocked());
            }
        });

        // set initial state
        lockToggleButton.setChecked(homeViewModel.isLocked());

        return root;
    }

    public void bluetoothSendLock() {
        bluetoothSetup();
        // TODO: send lock message to connected device
        homeViewModel.lock();
    }

    public void bluetoothSendUnlock() {
        bluetoothSetup();
        // TODO: send unlock message to connected device
        homeViewModel.unlock();
    }

    public void bluetoothSetup() {
        bluetoothEnable();
        // TODO: pair a device if no required device exists
        // TODO: connect to the appropriate paired device
    }

    public void bluetoothEnable() {
        if (!BA.isEnabled()) {
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn, REQUEST_ENABLE_BT);
        }
    }

    public void bluetoothDisable() {
        BA.disable();
    }

    public void bluetoothDiscoverable() {
        Intent getDiscoverable = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        startActivityForResult(getDiscoverable, REQUEST_DISCOVERY_BT);
    }
}