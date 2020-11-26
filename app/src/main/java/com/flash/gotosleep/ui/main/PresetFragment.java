package com.flash.gotosleep.ui.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flash.gotosleep.MainActivity;
import com.flash.gotosleep.R;

import java.util.Calendar;

public class PresetFragment extends Fragment {

    private TimeFragment timeFragment = new TimeFragment();

    private int activePreset = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_preset, container, false);

        MainActivity.preset1 = view.findViewById(R.id.preset1);
        MainActivity.preset2 = view.findViewById(R.id.preset2);
        MainActivity.preset3 = view.findViewById(R.id.preset3);

        changeToCurrentPreset();

        MainActivity.preset1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MainActivity.stopExecution) {
                    activePreset = 1;

                    MainActivity.preset1.setColorFilter(getResources().getColor(R.color.green));
                    MainActivity.preset2.setColorFilter(getResources().getColor(R.color.white));
                    MainActivity.preset3.setColorFilter(getResources().getColor(R.color.white));

                    saveData();
                    loadTimeAndSettings();
                }
            }
        });

        MainActivity.preset2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MainActivity.stopExecution) {
                    activePreset = 2;

                    MainActivity.preset2.setColorFilter(getResources().getColor(R.color.green));
                    MainActivity.preset1.setColorFilter(getResources().getColor(R.color.white));
                    MainActivity.preset3.setColorFilter(getResources().getColor(R.color.white));

                    saveData();
                    loadTimeAndSettings();
                }
            }
        });

        MainActivity.preset3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MainActivity.stopExecution) {
                    activePreset = 3;

                    MainActivity.preset3.setColorFilter(getResources().getColor(R.color.green));
                    MainActivity.preset1.setColorFilter(getResources().getColor(R.color.white));
                    MainActivity.preset2.setColorFilter(getResources().getColor(R.color.white));

                    saveData();
                    loadTimeAndSettings();
                }
            }
        });

        return view;
    }

    private void loadTimeAndSettings () {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MainActivity.SHARED_PREFS, Context.MODE_PRIVATE);

        MainActivity.muteSound.setChecked(sharedPreferences.getBoolean(MainActivity.muteSoundSwitchTemp + activePreset, false));
        MainActivity.vibrate.setChecked(sharedPreferences.getBoolean(MainActivity.vibrateSwitchTemp + activePreset, false));
        MainActivity.screenFlash.setChecked(sharedPreferences.getBoolean(MainActivity.screenFlashSwitchTemp + activePreset, false));

        timeFragment.t1Hour = sharedPreferences.getInt(timeFragment.t1HourTemp + activePreset, 0);
        timeFragment.t1Minute = sharedPreferences.getInt(timeFragment.t1MinTemp + activePreset, 0);
        timeFragment.t2Hour = sharedPreferences.getInt(timeFragment.t2HourTemp + activePreset, 6);
        timeFragment.t2Minute = sharedPreferences.getInt(timeFragment.t2MinTemp + activePreset, 0);

        Calendar calendar = Calendar.getInstance();

        calendar.set(0, 0, 0, timeFragment.t1Hour, timeFragment.t1Minute);
        TimeFragment.startTime.setText(DateFormat.format("HH:mm", calendar));

        calendar.set(0, 0, 0, timeFragment.t2Hour, timeFragment.t2Minute);
        TimeFragment.endTime.setText(DateFormat.format("HH:mm", calendar));

    }

    private void loadData () {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MainActivity.SHARED_PREFS, Context.MODE_PRIVATE);

        activePreset = sharedPreferences.getInt(MainActivity.ACTIVE_PRESET, 1);
    }

    private void saveData () {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MainActivity.SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(MainActivity.ACTIVE_PRESET, activePreset).apply();
    }

    private void changeToCurrentPreset () {
        loadData();

        switch (activePreset) {
            case 1:
                if (MainActivity.stopExecution) {
                    MainActivity.preset1.setColorFilter(getResources().getColor(R.color.green));
                    MainActivity.preset2.setColorFilter(getResources().getColor(R.color.white));
                    MainActivity.preset3.setColorFilter(getResources().getColor(R.color.white));
                }else {
                    MainActivity.preset1.setColorFilter(getResources().getColor(R.color.greenGrey));
                    MainActivity.preset2.setColorFilter(getResources().getColor(R.color.grey));
                    MainActivity.preset3.setColorFilter(getResources().getColor(R.color.grey));
                }

                break;
            case 2:
                if (MainActivity.stopExecution) {
                    MainActivity.preset2.setColorFilter(getResources().getColor(R.color.green));
                    MainActivity.preset1.setColorFilter(getResources().getColor(R.color.white));
                    MainActivity.preset3.setColorFilter(getResources().getColor(R.color.white));
                }else {
                    MainActivity.preset2.setColorFilter(getResources().getColor(R.color.greenGrey));
                    MainActivity.preset1.setColorFilter(getResources().getColor(R.color.grey));
                    MainActivity.preset3.setColorFilter(getResources().getColor(R.color.grey));
                }
                break;
            case 3:
                if (MainActivity.stopExecution) {
                    MainActivity.preset3.setColorFilter(getResources().getColor(R.color.green));
                    MainActivity.preset1.setColorFilter(getResources().getColor(R.color.white));
                    MainActivity.preset2.setColorFilter(getResources().getColor(R.color.white));
                }else {
                    MainActivity.preset3.setColorFilter(getResources().getColor(R.color.greenGrey));
                    MainActivity.preset1.setColorFilter(getResources().getColor(R.color.grey));
                    MainActivity.preset2.setColorFilter(getResources().getColor(R.color.grey));
                }
                break;
        }
    }
}