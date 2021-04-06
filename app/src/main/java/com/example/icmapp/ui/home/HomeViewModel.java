package com.example.icmapp.ui.home;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {
    private static final String TAG = "HomeView";

    private MutableLiveData<String> mText;
    private Boolean locked;

    public HomeViewModel() {
        mText = new MutableLiveData<>();

        mText.setValue("This is home fragment");
        locked = false;
    }

    public LiveData<String> getText() {
        return mText;
    }

    public Boolean isLocked() { return locked; }

    public Boolean lock() {
        Log.v(TAG, "sending a lock signal");
        locked = true;
        return true; // return false if it failed to send the signal over bluetooth
    }

    public Boolean unlock() {
        Log.v(TAG, "sending an unlock signal");
        locked = false;
        return true; // return false if it failed to send the signal over bluetooth
    }
}