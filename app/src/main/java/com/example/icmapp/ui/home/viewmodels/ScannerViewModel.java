package com.example.icmapp.ui.home.viewmodels;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import java.util.List;
import com.example.icmapp.adapter.DevicesLiveData;
import com.example.icmapp.adapter.PermissionsUtil;
import com.example.icmapp.adapter.ScannerLiveData;

import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat;
import no.nordicsemi.android.support.v18.scanner.ScanCallback;
import no.nordicsemi.android.support.v18.scanner.ScanResult;
import no.nordicsemi.android.support.v18.scanner.ScanSettings;

// taken from here: https://github.com/NordicSemiconductor/Android-nRF-Blinky/blob/2f3a06bf0e0c62a8172ac7fe2d872ac62ec19d84/app/src/main/java/no/nordicsemi/android/blinky/viewmodels/ScannerViewModel.java

public class ScannerViewModel extends AndroidViewModel {
    private static final String PREFS_FILTER_UUID_REQUIRED = "filter_uuid";
    private static final String PREFS_FILTER_NEARBY_ONLY = "filter_nearby";

    /**
     * MutableLiveData containing the list of devices.
     */
    private final DevicesLiveData devicesLiveData;
    /**
     * MutableLiveData containing the scanner state.
     */
    private final ScannerLiveData scannerLiveData;

    private final SharedPreferences preferences;

    public DevicesLiveData getDevices() {
        return devicesLiveData;
    }

    public ScannerLiveData getScannerState() {
        return scannerLiveData;
    }

    public ScannerViewModel(final Application application) {
        super(application);
        preferences = PreferenceManager.getDefaultSharedPreferences(application);

        final boolean filterUuidRequired = isUuidFilterEnabled();
        final boolean filerNearbyOnly = isNearbyFilterEnabled();

        scannerLiveData = new ScannerLiveData(PermissionsUtil.isBleEnabled(),
                PermissionsUtil.isLocationEnabled(application));
        devicesLiveData = new DevicesLiveData(filterUuidRequired, filerNearbyOnly);
        registerBroadcastReceivers(application);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        getApplication().unregisterReceiver(bluetoothStateBroadcastReceiver);

        if (PermissionsUtil.isMarshmallowOrAbove()) {
            getApplication().unregisterReceiver(locationProviderChangedReceiver);
        }
    }

    public boolean isUuidFilterEnabled() {
        return preferences.getBoolean(PREFS_FILTER_UUID_REQUIRED, true);
    }

    public boolean isNearbyFilterEnabled() {
        return preferences.getBoolean(PREFS_FILTER_NEARBY_ONLY, false);
    }

    /**
     * Forces the observers to be notified. This method is used to refresh the screen after the
     * location permission has been granted.
     */
    public void refresh() {
        scannerLiveData.refresh();
    }

    /**
     * Updates the device filter. Devices that once passed the filter will still be shown
     * even if they move away from the phone, or change the advertising packet. This is to
     * avoid removing devices from the list.
     *
     * @param uuidRequired if true, the list will display only devices with Led-Button Service UUID
     *                     in the advertising packet.
     */
    public void filterByUuid(final boolean uuidRequired) {
        preferences.edit().putBoolean(PREFS_FILTER_UUID_REQUIRED, uuidRequired).apply();
        if (devicesLiveData.filterByUuid(uuidRequired))
            scannerLiveData.recordFound();
        else
            scannerLiveData.clearRecords();
    }

    /**
     * Updates the device filter. Devices that once passed the filter will still be shown
     * even if they move away from the phone, or change the advertising packet. This is to
     * avoid removing devices from the list.
     *
     * @param nearbyOnly if true, the list will show only devices with high RSSI.
     */
    public void filterByDistance(final boolean nearbyOnly) {
        preferences.edit().putBoolean(PREFS_FILTER_NEARBY_ONLY, nearbyOnly).apply();
        if (devicesLiveData.filterByDistance(nearbyOnly))
            scannerLiveData.recordFound();
        else
            scannerLiveData.clearRecords();
    }

    /**
     * Start scanning for Bluetooth devices.
     */
    public void startScan() {
        if (scannerLiveData.isScanning()) {
            return;
        }

        // Scanning settings
        final ScanSettings settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .setReportDelay(500)
                .setUseHardwareBatchingIfSupported(false)
                .build();

        final BluetoothLeScannerCompat scanner = BluetoothLeScannerCompat.getScanner();
        scanner.startScan(null, settings, scanCallback);
        scannerLiveData.scanningStarted();
    }

    /**
     * Stop scanning for bluetooth devices.
     */
    public void stopScan() {
        if (scannerLiveData.isScanning() && scannerLiveData.isBluetoothEnabled()) {
            final BluetoothLeScannerCompat scanner = BluetoothLeScannerCompat.getScanner();
            scanner.stopScan(scanCallback);
            scannerLiveData.scanningStopped();
        }
    }

    private final ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(final int callbackType, @NonNull final ScanResult result) {
            // This callback will be called only if the scan report delay is not set or is set to 0.

            // If the packet has been obtained while Location was disabled, mark Location as not required
            if (PermissionsUtil.isLocationRequired(getApplication()) && !PermissionsUtil.isLocationEnabled(getApplication()))
                PermissionsUtil.markLocationNotRequired(getApplication());

            if (devicesLiveData.deviceDiscovered(result)) {
                devicesLiveData.applyFilter();
                scannerLiveData.recordFound();
            }
        }

        @Override
        public void onBatchScanResults(@NonNull final List<ScanResult> results) {
            // This callback will be called only if the report delay set above is greater then 0.

            // If the packet has been obtained while Location was disabled, mark Location as not required
            if (PermissionsUtil.isLocationRequired(getApplication()) && !PermissionsUtil.isLocationEnabled(getApplication()))
                PermissionsUtil.markLocationNotRequired(getApplication());

            boolean atLeastOneMatchedFilter = false;
            for (final ScanResult result : results)
                atLeastOneMatchedFilter = devicesLiveData.deviceDiscovered(result) || atLeastOneMatchedFilter;
            if (atLeastOneMatchedFilter) {
                devicesLiveData.applyFilter();
                scannerLiveData.recordFound();
            }
        }

        @Override
        public void onScanFailed(final int errorCode) {
            // TODO This should be handled
            scannerLiveData.scanningStopped();
        }
    };

    /**
     * Register for required broadcast receivers.
     */
    private void registerBroadcastReceivers(@NonNull final Application application) {
        application.registerReceiver(bluetoothStateBroadcastReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
        if (PermissionsUtil.isMarshmallowOrAbove()) {
            application.registerReceiver(locationProviderChangedReceiver, new IntentFilter(LocationManager.MODE_CHANGED_ACTION));
        }
    }

    /**
     * Broadcast receiver to monitor the changes in the location provider.
     */
    private final BroadcastReceiver locationProviderChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            final boolean enabled = PermissionsUtil.isLocationEnabled(context);
            scannerLiveData.setLocationEnabled(enabled);
        }
    };

    /**
     * Broadcast receiver to monitor the changes in the bluetooth adapter.
     */
    private final BroadcastReceiver bluetoothStateBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF);
            final int previousState = intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_STATE, BluetoothAdapter.STATE_OFF);

            switch (state) {
                case BluetoothAdapter.STATE_ON:
                    scannerLiveData.bluetoothEnabled();
                    break;
                case BluetoothAdapter.STATE_TURNING_OFF:
                case BluetoothAdapter.STATE_OFF:
                    if (previousState != BluetoothAdapter.STATE_TURNING_OFF && previousState != BluetoothAdapter.STATE_OFF) {
                        stopScan();
                        scannerLiveData.bluetoothDisabled();
                    }
                    break;
            }
        }
    };
}
}
