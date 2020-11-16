package com.example.gotosleep.ui.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.gotosleep.MainActivity;
import com.example.gotosleep.R;

import java.util.Calendar;

public class PresetFragment extends Fragment {

    private ImageView preset1, preset2, preset3;

    private MainActivity mainActivity = new MainActivity();
    private TimeFragment timeFragment = new TimeFragment();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_preset, container, false);

        preset1 = view.findViewById(R.id.preset1);
        preset2 = view.findViewById(R.id.preset2);
        preset3 = view.findViewById(R.id.preset3);

        changeToCurrentPreset();

        preset1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MainActivity.stopExecution) {
                    mainActivity.currentActivePreset = 1;

                    preset1.setColorFilter(ContextCompat.getColor(getContext(), R.color.green), PorterDuff.Mode.SRC_ATOP);
                    preset2.setColorFilter(ContextCompat.getColor(getContext(), R.color.white), PorterDuff.Mode.SRC_ATOP);
                    preset3.setColorFilter(ContextCompat.getColor(getContext(), R.color.white), PorterDuff.Mode.SRC_ATOP);

                    saveData();
                    loadTimeAndSettings();
                }else {
                    preset1.setColorFilter(ContextCompat.getColor(getContext(), R.color.grey), PorterDuff.Mode.SRC_ATOP);
                    preset2.setColorFilter(ContextCompat.getColor(getContext(), R.color.grey), PorterDuff.Mode.SRC_ATOP);
                    preset3.setColorFilter(ContextCompat.getColor(getContext(), R.color.grey), PorterDuff.Mode.SRC_ATOP);
                }
            }
        });

        preset2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MainActivity.stopExecution) {
                    mainActivity.currentActivePreset = 2;

                    preset2.setColorFilter(ContextCompat.getColor(getContext(), R.color.green), PorterDuff.Mode.SRC_ATOP);
                    preset1.setColorFilter(ContextCompat.getColor(getContext(), R.color.white), PorterDuff.Mode.SRC_ATOP);
                    preset3.setColorFilter(ContextCompat.getColor(getContext(), R.color.white), PorterDuff.Mode.SRC_ATOP);

                    saveData();
                    loadTimeAndSettings();
                }
            }
        });

        preset3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MainActivity.stopExecution) {
                    mainActivity.currentActivePreset = 3;

                    preset3.setColorFilter(ContextCompat.getColor(getContext(), R.color.green), PorterDuff.Mode.SRC_ATOP);
                    preset1.setColorFilter(ContextCompat.getColor(getContext(), R.color.white), PorterDuff.Mode.SRC_ATOP);
                    preset2.setColorFilter(ContextCompat.getColor(getContext(), R.color.white), PorterDuff.Mode.SRC_ATOP);

                    saveData();
                    loadTimeAndSettings();
                }
            }
        });

        return view;
    }

    private void loadTimeAndSettings () {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MainActivity.SHARED_PREFS, Context.MODE_PRIVATE);

        MainActivity.muteSound.setChecked(sharedPreferences.getBoolean(MainActivity.muteSoundSwitchTemp + mainActivity.currentActivePreset, false));
        MainActivity.vibrate.setChecked(sharedPreferences.getBoolean(MainActivity.vibrateSwitchTemp + mainActivity.currentActivePreset, false));
        MainActivity.screenFlash.setChecked(sharedPreferences.getBoolean(MainActivity.screenFlashSwitchTemp + mainActivity.currentActivePreset, false));

        timeFragment.t1Hour = sharedPreferences.getInt(timeFragment.t1HourTemp + mainActivity.currentActivePreset, 0);
        timeFragment.t1Minute = sharedPreferences.getInt(timeFragment.t1MinTemp + mainActivity.currentActivePreset, 0);
        timeFragment.t2Hour = sharedPreferences.getInt(timeFragment.t2HourTemp + mainActivity.currentActivePreset, 6);
        timeFragment.t2Minute = sharedPreferences.getInt(timeFragment.t2MinTemp + mainActivity.currentActivePreset, 0);

        Calendar calendar = Calendar.getInstance();

        calendar.set(0, 0, 0, timeFragment.t1Hour, timeFragment.t1Minute);
        TimeFragment.startTime.setText(DateFormat.format("HH:mm", calendar));

        calendar.set(0, 0, 0, timeFragment.t2Hour, timeFragment.t2Minute);
        TimeFragment.endTime.setText(DateFormat.format("HH:mm", calendar));

    }

    private void loadData () {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MainActivity.SHARED_PREFS, Context.MODE_PRIVATE);

        mainActivity.currentActivePreset = sharedPreferences.getInt(MainActivity.ACTIVE_PRESET, 1);
    }

    private void saveData () {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MainActivity.SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(MainActivity.ACTIVE_PRESET, mainActivity.currentActivePreset).apply();
    }

    private void changeToCurrentPreset () {
        loadData();

        switch (mainActivity.currentActivePreset) {
            case 1:
                preset1.setColorFilter(ContextCompat.getColor(getContext(), R.color.green), PorterDuff.Mode.SRC_ATOP);
                preset2.setColorFilter(ContextCompat.getColor(getContext(), R.color.white), PorterDuff.Mode.SRC_ATOP);
                preset3.setColorFilter(ContextCompat.getColor(getContext(), R.color.white), PorterDuff.Mode.SRC_ATOP);
                break;
            case 2:
                preset2.setColorFilter(ContextCompat.getColor(getContext(), R.color.green), PorterDuff.Mode.SRC_ATOP);
                preset1.setColorFilter(ContextCompat.getColor(getContext(), R.color.white), PorterDuff.Mode.SRC_ATOP);
                preset3.setColorFilter(ContextCompat.getColor(getContext(), R.color.white), PorterDuff.Mode.SRC_ATOP);
                break;
            case 3:
                preset3.setColorFilter(ContextCompat.getColor(getContext(), R.color.green), PorterDuff.Mode.SRC_ATOP);
                preset1.setColorFilter(ContextCompat.getColor(getContext(), R.color.white), PorterDuff.Mode.SRC_ATOP);
                preset2.setColorFilter(ContextCompat.getColor(getContext(), R.color.white), PorterDuff.Mode.SRC_ATOP);
                break;
        }
    }
}