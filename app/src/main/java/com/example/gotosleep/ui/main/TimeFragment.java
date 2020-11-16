package com.example.gotosleep.ui.main;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gotosleep.MainActivity;
import com.example.gotosleep.R;

import java.util.Calendar;

public class TimeFragment extends Fragment
{
    public static final String T1HOUR_1 = "t1Hour1";
    public static final String T1MINUTE_1 = "t1Minute1";
    public static final String T2HOUR_1 = "t2Hour1";
    public static final String T2MINUTE_1 = "t2Minute1";

    public static final String T1HOUR_2 = "t1Hour2";
    public static final String T1MINUTE_2 = "t1Minute2";
    public static final String T2HOUR_2 = "t2Hour2";
    public static final String T2MINUTE_2 = "t2Minute2";

    public static final String T1HOUR_3 = "t1Hour3";
    public static final String T1MINUTE_3 = "t1Minute3";
    public static final String T2HOUR_3 = "t2Hour3";
    public static final String T2MINUTE_3 = "t2Minute3";

    public String t1HourTemp = "T1HOUR_";
    public String t1MinTemp = "T1MINUTE_";
    public String t2HourTemp = "T2HOUR_";
    public String t2MinTemp = "T2MINUTE_";

    private int currentPreset = 1;

    public static TextView startTime, endTime;
    public int t1Hour, t1Minute, t2Hour, t2Minute;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        startTime = view.findViewById(R.id.startTime);
        endTime = view.findViewById(R.id.endTime);

        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        getActivity(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                                t1Hour = hour;
                                t1Minute = minute;

                                Calendar calendar = Calendar.getInstance();

                                calendar.set(0, 0, 0, t1Hour, t1Minute);

                                startTime.setText(DateFormat.format("HH:mm", calendar));

                                saveData();
                            }
                        }, 24, 0, true
                );

                timePickerDialog.updateTime(t1Hour, t1Minute);
                timePickerDialog.show();
            }
        });

        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        getActivity(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                                t2Hour = hour;
                                t2Minute = minute;

                                Calendar calendar = Calendar.getInstance();

                                calendar.set(0, 0, 0, t2Hour, t2Minute);

                                endTime.setText(DateFormat.format("HH:mm", calendar));

                                saveData();
                            }
                        }, 24, 0, true
                );

                timePickerDialog.updateTime(t2Hour, t2Minute);
                timePickerDialog.show();
            }
        });

        loadData();

        return view;
    }

    @Override
    public void onStop() {
        super.onStop();

        saveData();
    }

    public void saveData() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MainActivity.SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        currentPreset = sharedPreferences.getInt(MainActivity.ACTIVE_PRESET, 1);

        editor.putInt(t1HourTemp + currentPreset, t1Hour);
        editor.putInt(t1MinTemp + currentPreset, t1Minute);
        editor.putInt(t2HourTemp + currentPreset, t2Hour);
        editor.putInt(t2MinTemp + currentPreset, t2Minute);

        editor.apply();
    }

    public void loadData() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MainActivity.SHARED_PREFS, Context.MODE_PRIVATE);

        currentPreset = sharedPreferences.getInt(MainActivity.ACTIVE_PRESET, 1);

        t1Hour = sharedPreferences.getInt(t1HourTemp + currentPreset, 0);
        t1Minute = sharedPreferences.getInt(t1MinTemp + currentPreset, 0);
        t2Hour = sharedPreferences.getInt(t2HourTemp + currentPreset, 6);
        t2Minute = sharedPreferences.getInt(t2MinTemp + currentPreset, 0);

        Calendar calendar = Calendar.getInstance();

        calendar.set(0, 0, 0, t1Hour, t1Minute);
        startTime.setText(DateFormat.format("HH:mm", calendar));

        calendar.set(0, 0, 0, t2Hour, t2Minute);
        endTime.setText(DateFormat.format("HH:mm", calendar));
    }
}