package com.example.icmapp.ICMprofile;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.icmapp.ICMprofile.data.ICMLock;
import com.example.icmapp.adapter.DevicesLiveData;

import java.util.UUID;

import no.nordicsemi.android.ble.BleManager;
import no.nordicsemi.android.ble.data.Data;

// reference: https://github.com/NordicSemiconductor/Android-BLE-Library/blob/master/README.md#usage
public class ICMManager extends BleManager {
    public final static UUID SERVICE_UUID      = UUID.fromString("b3a9b76b-e3cc-46cd-adc7-ceeba9977b0f");
    public final static UUID LOCK_CHAR_UUID    = UUID.fromString("d1dcac5b-8961-4917-ac9d-b59b36351594");

    boolean DEBUG = true;

    private BluetoothGattCharacteristic lockChar;
    private final MutableLiveData<Boolean> lockedStatus  = new MutableLiveData<>();

    private boolean locked = false;

    public ICMManager(@NonNull Context context) {
        super(context);
    }

    public final LiveData<Boolean> isLocked() { return lockedStatus; }

    private final ICMLockCallback lockCallback = new ICMBooleanCallback() {
        @Override
        public void onBooleanStateChange(@NonNull BluetoothDevice device, boolean state) {
            lockedStatus.setValue(state);
            locked = state;
        }

        @Override
        public void onInvalidDataReceived(@NonNull final BluetoothDevice device, @NonNull final Data data) {
            log(Log.WARN, "Invalid data received for lock characteristic: " + data);
        }
    }

    @NonNull
    @Override
    protected BleManagerGattCallback getGattCallback() {
        return new IcmGattCallback();
    }

    @Override
    public void log(final int priority, @NonNull final String message) {
        if (DEBUG || priority == Log.ERROR) {
            Log.println(priority, "IcmBleManager", message);
        }
    }

    private class IcmGattCallback extends BleManagerGattCallback {
        final int READWRITE = BluetoothGattCharacteristic.PROPERTY_READ
                            & BluetoothGattCharacteristic.PROPERTY_WRITE
                            & BluetoothGattCharacteristic.PROPERTY_NOTIFY; // for server updates

        @Override
        protected void initialize() {
            readCharacteristic(lockChar).with(lockCallback).enqueue();
        }

        @Override
        public boolean isRequiredServiceSupported(@NonNull final BluetoothGatt gatt) {
            final BluetoothGattService service = gatt.getService(SERVICE_UUID);
            if(service != null) {
                lockChar = service.getCharacteristic(LOCK_CHAR_UUID);
            }

            // checking if we can read/write this characteristic
            boolean lockReadWrite = false;
            if(lockChar != null) {
                final int properties = lockChar.getProperties();
                lockReadWrite = (properties & READWRITE) != 0;
            }

            return lockChar != null && lockReadWrite;
        }

        @Override
        protected void onDeviceDisconnected() {
            lockChar = null;
        }
    }

    public void setLock(final boolean lock) {
        // Are we connected?
        if (lockChar == null) return;

        // No need to change?
        if (locked == lock) return;

        lockedStatus.setValue(lock);

        log(Log.INFO, "ICM " + (locked ? "locked" : "unlocked"));
        writeCharacteristic(lockChar, lock ? ICMLock.lock() : ICMLock.unlock())
            .with(lockCallback).enqueue();
    }
}
