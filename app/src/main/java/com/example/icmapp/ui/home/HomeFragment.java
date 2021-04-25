package com.example.icmapp.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.icmapp.R;
import com.example.icmapp.ui.home.viewmodels.HomeViewModel;

public class HomeFragment extends Fragment {
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_DISCOVERY_BT = 2;

    private HomeViewModel homeViewModel;
    private ToggleButton lockToggleButton;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        // gets refs to elements in home view
        lockToggleButton = (ToggleButton) root.findViewById(R.id.lockToggleButton);

        // set callbacks
        lockToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                homeViewModel.setLock(isChecked);
            }
        });

        return root;
    }

}