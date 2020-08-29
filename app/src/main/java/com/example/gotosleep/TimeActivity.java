package com.example.gotosleep;

import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

public class TimeActivity extends AppCompatActivity {

    private MainActivity mainActivity = new MainActivity();
    private TextView startTime, endTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadData();

        if (!mainActivity.tutorialComplete) {
            setContentView(R.layout.activity_time);

            startTime = findViewById(R.id.startTime);
            endTime = findViewById(R.id.endTime);

            startTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TimePickerDialog timePickerDialog = new TimePickerDialog(
                            TimeActivity.this,
                            new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                                    mainActivity.t1Hour = hour;
                                    mainActivity.t1Minute = minute;

                                    Calendar calendar = Calendar.getInstance();

                                    calendar.set(0, 0, 0, mainActivity.t1Hour, mainActivity.t1Minute);

                                    startTime.setText(DateFormat.format("hh:mm aa", calendar));
                                }
                            }, 12, 0, false
                    );


                    timePickerDialog.updateTime(mainActivity.t1Hour, mainActivity.t1Minute);
                    timePickerDialog.show();
                }
            });

            endTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TimePickerDialog timePickerDialog = new TimePickerDialog(
                            TimeActivity.this,
                            new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                                    mainActivity.t2Hour = hour;
                                    mainActivity.t2Minute = minute;

                                    Calendar calendar = Calendar.getInstance();

                                    calendar.set(0, 0, 0, mainActivity.t2Hour, mainActivity.t2Minute);

                                    endTime.setText(DateFormat.format("hh:mm aa", calendar));
                                }
                            }, 12, 0, false
                    );

                    timePickerDialog.updateTime(mainActivity.t2Hour, mainActivity.t2Minute);
                    timePickerDialog.show();
                }
            });
        }else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        saveData();
    }

    public void saveData () {
        SharedPreferences sharedPreferences = getSharedPreferences(mainActivity.SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(mainActivity.T1HOUR, mainActivity.t1Hour);
        editor.putInt(mainActivity.T1MINUTE, mainActivity.t1Minute);
        editor.putInt(mainActivity.T2HOUR, mainActivity.t2Hour);
        editor.putInt(mainActivity.T2MINUTE, mainActivity.t2Minute);

        editor.apply();
    }

    public void loadData () {
        SharedPreferences sharedPreferences = getSharedPreferences(mainActivity.SHARED_PREFS, MODE_PRIVATE);

        mainActivity.t1Hour = sharedPreferences.getInt(mainActivity.T1HOUR, 0);
        mainActivity.t1Minute = sharedPreferences.getInt(mainActivity.T1MINUTE, 0);
        mainActivity.t2Hour = sharedPreferences.getInt(mainActivity.T2HOUR, 6);
        mainActivity.t2Minute = sharedPreferences.getInt(mainActivity.T2MINUTE, 0);
        mainActivity.tutorialComplete = sharedPreferences.getBoolean(mainActivity.TUTORIAL_COMPLETE, false);

        if (startTime != null) {
            startTime.setText(mainActivity.formatTimeString(mainActivity.t1Hour, mainActivity.t1Minute));
            endTime.setText(mainActivity.formatTimeString(mainActivity.t2Hour, mainActivity.t2Minute));
        }
    }

    public void goToSettingsActivity (View view) {
        saveData();

        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}

