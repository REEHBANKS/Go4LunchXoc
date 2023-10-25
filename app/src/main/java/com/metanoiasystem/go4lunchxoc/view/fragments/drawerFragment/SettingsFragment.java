package com.metanoiasystem.go4lunchxoc.view.fragments.drawerFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.metanoiasystem.go4lunchxoc.databinding.FragmentAccountBinding;
import com.metanoiasystem.go4lunchxoc.databinding.FragmentSettingsBinding;
import com.metanoiasystem.go4lunchxoc.utils.Injector;

public class SettingsFragment extends DialogFragment {

    private FragmentSettingsBinding binding;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(getLayoutInflater(), container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.closeWindowsSettingFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        binding.alarmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Toast.makeText(getContext(), "notification off", Toast.LENGTH_SHORT).show();
                }else {

                    Toast.makeText(getContext(), "notification on", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }



}
