package com.example.icmapp.ui.home;

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
                if (isChecked) {
                    homeViewModel.lock();
                } else {
                    homeViewModel.unlock();
                }
            }
        });

        // set initial state
        lockToggleButton.setChecked(homeViewModel.isLocked());

        return root;
    }
}