package com.example.gotosleep.ui.main;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateFormat;
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
    public static final String T1HOUR = "t1Hour";
    public static final String T1MINUTE = "t1Minute";
    public static final String T2HOUR = "t2Hour";
    public static final String T2MINUTE = "t2Minute";

    public TextView startTime, endTime;
    public int t1Hour, t1Minute, t2Hour, t2Minute;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        startTime = view.findViewById(R.id.startTime2);
        endTime = view.findViewById(R.id.endTime2);

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

        editor.putInt(T1HOUR, t1Hour);
        editor.putInt(T1MINUTE, t1Minute);
        editor.putInt(T2HOUR, t2Hour);
        editor.putInt(T2MINUTE, t2Minute);

        editor.apply();
    }

    public void loadData() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MainActivity.SHARED_PREFS, Context.MODE_PRIVATE);

        t1Hour = sharedPreferences.getInt(T1HOUR, 0);
        t1Minute = sharedPreferences.getInt(T1MINUTE, 0);
        t2Hour = sharedPreferences.getInt(T2HOUR, 6);
        t2Minute = sharedPreferences.getInt(T2MINUTE, 0);

        Calendar calendar = Calendar.getInstance();

        calendar.set(0, 0, 0, t1Hour, t1Minute);
        startTime.setText(DateFormat.format("HH:mm", calendar));

        calendar.set(0, 0, 0, t2Hour, t2Minute);
        endTime.setText(DateFormat.format("HH:mm", calendar));
    }


}