package com.example.icmapp.adapter;

import androidx.lifecycle.LiveData;

public class ScannerLiveData extends LiveData<ScannerLiveData> {
    private boolean scanningStarted;
    private boolean hasRecords;
    private boolean bluetoothEnabled;
    private boolean locationEnabled;

    public ScannerLiveData(final boolean bluetoothEnabled, final boolean locationEnabled) {
        this.scanningStarted = false;
        this.bluetoothEnabled = bluetoothEnabled;
        this.locationEnabled = locationEnabled;
        refresh();
    }

    public void refresh() {
        postValue(this);
    }

    public void scanningStarted() {
        scanningStarted = true;
        refresh();
    }

    public void scanningStopped() {
        scanningStarted = false;
        refresh();
    }

    public void bluetoothEnabled() {
        bluetoothEnabled = true;
        refresh();
    }

    public void bluetoothDisabled() {
        bluetoothEnabled = false;
        hasRecords =  false;
        refresh();
    }

    public void recordFound() {
        hasRecords = true;
        refresh();
    }

    public void clearRecords() {
        hasRecords = false;
        refresh();
    }

    public void setLocationEnabled(final boolean enabled) {
        locationEnabled = enabled;
        refresh();
    }

    boolean isScanning() {
        return scanningStarted;
    }

    public boolean hasRecords() {
        return hasRecords;
    }

    public boolean isBluetoothEnabled() {
        return bluetoothEnabled;
    }

    public boolean isLocationEnabled() {
        return locationEnabled;
    }

    public boolean
}
