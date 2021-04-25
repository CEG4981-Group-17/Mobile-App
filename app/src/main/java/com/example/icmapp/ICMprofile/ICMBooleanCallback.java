package com.example.icmapp.ICMprofile;

import android.bluetooth.BluetoothDevice;

import androidx.annotation.NonNull;

public interface ICMBooleanCallback {
    void onBooleanStateChange(@NonNull final BluetoothDevice device, final boolean state);
}
