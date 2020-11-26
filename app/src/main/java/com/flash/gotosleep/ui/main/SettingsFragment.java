package com.flash.gotosleep.ui.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flash.gotosleep.ScreenFlashDialog;
import com.flash.gotosleep.MainActivity;
import com.flash.gotosleep.R;

public class SettingsFragment extends Fragment {

    private MainActivity mainActivity = new MainActivity();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        MainActivity.muteSound = view.findViewById(R.id.muteSoundSwitch);
        MainActivity.vibrate = view.findViewById(R.id.vibrateSwitch);
        MainActivity.screenFlash = view.findViewById(R.id.screenFlashSwitch);

        MainActivity.muteSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                saveData();
            }
        });

        MainActivity.vibrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                saveData();
            }
        });

        MainActivity.screenFlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!Settings.System.canWrite(getContext()) && MainActivity.screenFlash.isChecked()) {
                        // isChecked gets changed onclick, so if the switch is on and it gets pressed, isChecked outputs false
                        ScreenFlashDialog screenFlashDialog = new ScreenFlashDialog();
                        screenFlashDialog.show(getFragmentManager(), "info dialog");
                    }
                }

                saveData();
            }
        });

        loadData();

        mainActivity.mainButtonBehaviour(null);

        saveData();

        return view;
    }

    @Override
    public void onStop() {
        super.onStop();

        saveData();
    }

    public void saveData () {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MainActivity.SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        mainActivity.currentActivePreset = sharedPreferences.getInt(MainActivity.ACTIVE_PRESET, 1);

        editor.putBoolean(MainActivity.muteSoundSwitchTemp + mainActivity.currentActivePreset, MainActivity.muteSound.isChecked());
        editor.putBoolean(MainActivity.vibrateSwitchTemp + mainActivity.currentActivePreset, MainActivity.vibrate.isChecked());
        editor.putBoolean(MainActivity.screenFlashSwitchTemp + mainActivity.currentActivePreset, MainActivity.screenFlash.isChecked());
        editor.putBoolean(MainActivity.BUTTON_STATUS, mainActivity.buttonStatus);

        editor.apply();
    }

    public void loadData () {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MainActivity.SHARED_PREFS, Context.MODE_PRIVATE);

        mainActivity.currentActivePreset = sharedPreferences.getInt(MainActivity.ACTIVE_PRESET, 1);

        MainActivity.muteSound.setChecked(sharedPreferences.getBoolean(MainActivity.muteSoundSwitchTemp + mainActivity.currentActivePreset, false));
        MainActivity.vibrate.setChecked(sharedPreferences.getBoolean(MainActivity.vibrateSwitchTemp + mainActivity.currentActivePreset, false));
        MainActivity.screenFlash.setChecked(sharedPreferences.getBoolean(MainActivity.screenFlashSwitchTemp + mainActivity.currentActivePreset, false));
        mainActivity.buttonStatus = sharedPreferences.getBoolean(MainActivity.BUTTON_STATUS, false);
    }
}