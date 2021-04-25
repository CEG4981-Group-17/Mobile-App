package com.example.icmapp.ICMprofile;

import android.bluetooth.BluetoothDevice;

import androidx.annotation.NonNull;

import com.example.icmapp.ICMprofile.data.ICMLock;

import java.util.Arrays;

import no.nordicsemi.android.ble.callback.DataSentCallback;
import no.nordicsemi.android.ble.callback.profile.ProfileDataCallback;
import no.nordicsemi.android.ble.data.Data;

public abstract class ICMLockCallback implements ProfileDataCallback, DataSentCallback, ICMBooleanCallback {
    @Override
    public void onDataReceived(@NonNull final BluetoothDevice device, @NonNull final Data data) {
        parse(device, data);
    }

    @Override
    public void onDataSent(@NonNull final BluetoothDevice device, @NonNull final Data data) {
        parse(device, data);
    }

    public void parse(@NonNull final BluetoothDevice device, @NonNull final Data data) {
        if (data.size() != 1) {
            onInvalidDataReceived(device, data);
            return;
        }

        byte[] state = data.getValue();
        if (Arrays.equals(state, ICMLock.lock().getValue())) {
            onBooleanStateChange(device, true);
        } else if (Arrays.equals(state, ICMLock.unlock().getValue())) {
            onBooleanStateChange(device, false);
        } else {
            onInvalidDataReceived(device, data);
        }
    }
}