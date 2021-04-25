package com.example.icmapp.ICMprofile.data;

import androidx.annotation.NonNull;

import no.nordicsemi.android.ble.data.Data;

public final class ICMLock {
    private static final byte STATE_UNLOCKED = 0x00;
    private static final byte STATE_LOCKED = 0x01;

    @NonNull
    public static Data lock() {
        return Data.opCode(STATE_LOCKED);
    }

    public static Data unlock() {
        return Data.opCode(STATE_UNLOCKED);
    }
}
