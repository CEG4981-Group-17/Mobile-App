package com.example.icmapp.ui.home.viewmodels;

import android.bluetooth.BluetoothDevice;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.icmapp.ICMprofile.ICMManager;
import com.example.icmapp.adapter.DiscoveredBluetoothDevice;

public class HomeViewModel extends ViewModel {
    private ICMManager ICM;
    private BluetoothDevice device;

    public HomeViewModel() {
        ICM = new ICMManager();
    }

    public LiveData<Boolean> getLock() {
        return ICM.isLocked();
    }

    public boolean deviceConnected() {
        return device != null;
    }

    public void setLock(final boolean lock) {
        if(deviceConnected()) {
            ICM.setLock(lock);
        }
    }

    public void connect(@NonNull final DiscoveredBluetoothDevice target) {
        // Prevent from calling again when called again (screen orientation changed).
        if (!deviceConnected()) {
            device = target.getDevice();
            reconnect();
        }
    }

    public void reconnect() {
        if (deviceConnected()) {
            ICM.connect(device)
                    .retry(3, 100)
                    .useAutoConnect(false)
                    .enqueue();
        }
    }

    private void disconnect() {
        device = null;
        ICM.disconnect().enqueue();
    }
}