package com.example.gotosleep;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    public int t1Hour, t1Minute, t2Hour, t2Minute;

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String T1HOUR = "t1Hour";
    public static final String T1MINUTE = "t1Minute";
    public static final String T2HOUR = "t2Hour";
    public static final String T2MINUTE = "t2Minute";

    public static final String VIBRATE_SWTICH = "vibrateSwitch";
    public static final String MUTE_SOUND_SWITCH = "muteSoundSwitch";

    private Switch vibrate, muteSound;
    private TextView startTime, endTime;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startTime = findViewById(R.id.startTime);
        endTime = findViewById(R.id.endTime);
        vibrate = findViewById(R.id.vibrateSwitch);
        muteSound = findViewById(R.id.muteSoundSwitch);

        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        MainActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                                t1Hour = hour;
                                t1Minute = minute;

                                Calendar calendar = Calendar.getInstance();

                                calendar.set(0, 0, 0, t1Hour, t1Minute);

                                startTime.setText(DateFormat.format("hh:mm aa", calendar));
                            }
                        }, 12, 0, false
                );

                timePickerDialog.updateTime(t1Hour, t1Minute);
                timePickerDialog.show();
            }
        });

        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        MainActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                                t2Hour = hour;
                                t2Minute = minute;

                                Calendar calendar = Calendar.getInstance();

                                calendar.set(0, 0, 0, t2Hour, t2Minute);

                                endTime.setText(DateFormat.format("hh:mm aa", calendar));
                            }
                        }, 12, 0, false
                );

                timePickerDialog.updateTime(t2Hour, t2Minute);
                timePickerDialog.show();
            }
        });

        loadData();
    }

    @Override
    protected void onStop() {
        super.onStop();

        saveData();
    }

    public void saveData () {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean(MUTE_SOUND_SWITCH, muteSound.isChecked());
        editor.putBoolean(VIBRATE_SWTICH, vibrate.isChecked());
        editor.putInt(T1HOUR, t1Hour);
        editor.putInt(T1MINUTE, t1Minute);
        editor.putInt(T2HOUR, t2Hour);
        editor.putInt(T2MINUTE, t2Minute);

        editor.apply();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void loadData () {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

        t1Hour = sharedPreferences.getInt(T1HOUR, 0);
        t1Minute = sharedPreferences.getInt(T1MINUTE, 0);
        t2Hour = sharedPreferences.getInt(T2HOUR, 0);
        t2Minute = sharedPreferences.getInt(T2MINUTE, 0);

        startTime.setText(formatTimeString(t1Hour, t1Minute));
        endTime.setText(formatTimeString(t2Hour, t2Minute));

        vibrate.setChecked(sharedPreferences.getBoolean(VIBRATE_SWTICH, false));
        muteSound.setChecked(sharedPreferences.getBoolean(MUTE_SOUND_SWITCH, false));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String formatTimeString (int hour, int minute) {
        String output = "";

        if (hour < 10) {
            output += "0" + hour + ":";
        }else {
            output += hour + ":";
        }

        if (minute < 10) {
            output += "0" + minute;
        }else {
            output += minute;
        }

        output = LocalTime.parse(output).format(DateTimeFormatter.ofPattern("hh:mm a"));

        return output;
    }

    public void goToSettingsActivity (View view) {
        saveData();

        Intent intent = new Intent(this, TimeActivity.class);
        startActivity(intent);
    }
}