package com.example.gotosleep;

import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String T1HOUR = "t1Hour";
    public static final String T1MINUTE = "t1Minute";
    public static final String T2HOUR = "t2Hour";
    public static final String T2MINUTE = "t2Minute";
    public static final String VIBRATE_SWTICH = "vibrateSwitch";
    public static final String MUTE_SOUND_SWITCH = "muteSoundSwitch";
    public static final String TUTORIAL_COMPLETE = "tutorialComplete";
    public static final String BUTTON_STATUS = "buttonStatus";

    public boolean tutorialComplete = false;
    public int t1Hour, t1Minute, t2Hour, t2Minute;
    public Switch muteSound, vibrate;

    public static int controlValue = 0; // (controlValue <= 1) no tasks running or one task running, (controlValue > 1) too many tasks are running, destroy instances until there is only one left.
    public static boolean stopExecution = false;

    private boolean buttonStatus = true;
    private Button mainButton;
    private TextView startTime, endTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startTime = findViewById(R.id.startTime);
        endTime = findViewById(R.id.endTime);
        muteSound = findViewById(R.id.muteSoundSwitch);
        vibrate = findViewById(R.id.vibrateSwitch);
        mainButton = findViewById(R.id.mainButton);

        muteSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                saveData();
            }
        });

        vibrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                saveData();
            }
        });

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

                                saveData();
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

                                saveData();
                            }
                        }, 12, 0, false
                );

                timePickerDialog.updateTime(t2Hour, t2Minute);
                timePickerDialog.show();
            }
        });

        loadData();
        mainButtonBehaviour(null);
    }

    @Override
    protected void onStop() {
        super.onStop();

        saveData();
    }

    public void saveData () {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean(MUTE_SOUND_SWITCH, vibrate.isChecked());
        editor.putBoolean(VIBRATE_SWTICH, muteSound.isChecked());
        editor.putInt(T1HOUR, t1Hour);
        editor.putInt(T1MINUTE, t1Minute);
        editor.putInt(T2HOUR, t2Hour);
        editor.putInt(T2MINUTE, t2Minute);
        editor.putBoolean(TUTORIAL_COMPLETE, tutorialComplete);
        editor.putBoolean(BUTTON_STATUS, buttonStatus);

        editor.apply();
    }

    public void loadData () {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

        t1Hour = sharedPreferences.getInt(T1HOUR, 0);
        t1Minute = sharedPreferences.getInt(T1MINUTE, 0);
        t2Hour = sharedPreferences.getInt(T2HOUR, 0);
        t2Minute = sharedPreferences.getInt(T2MINUTE, 0);
        tutorialComplete = sharedPreferences.getBoolean(TUTORIAL_COMPLETE, false);
        buttonStatus = sharedPreferences.getBoolean(BUTTON_STATUS, true);

        startTime.setText(formatTimeString(t1Hour, t1Minute));
        endTime.setText(formatTimeString(t2Hour, t2Minute));

        muteSound.setChecked(sharedPreferences.getBoolean(VIBRATE_SWTICH, false));
        vibrate.setChecked(sharedPreferences.getBoolean(MUTE_SOUND_SWITCH, false));
    }

    public String formatTimeString (int hour, int minute) {
        String output = "";

        String timeOfDay = "";

        if (hour > 12) {
            hour = hour - 12;

            timeOfDay = "PM";
        }else {
            timeOfDay = "AM";

            if (hour == 0) {
                hour = 12;
            }
        }

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

        output += " " + timeOfDay;

        return output;
    }

    public void mainButtonBehaviour (View view) {
        saveData();

        if (buttonStatus) {
            if (view != null) {
                mainButton.setText("OFF");
                mainButton.setBackgroundResource(R.drawable.roundedbuttonred);
                buttonStatus = false;

                vibrate.setEnabled(true);
                muteSound.setEnabled(true);

                stopTimeChecking();
            }else {
                mainButton.setText("ON");
                mainButton.setBackgroundResource(R.drawable.roundedbuttongreen);

                vibrate.setEnabled(false);
                muteSound.setEnabled(false);

                if (!tutorialComplete) {
                    startTimeChecking();
                    tutorialComplete = true;
                }
            }
        }else {
            if (view != null) {
                mainButton.setText("ON");
                mainButton.setBackgroundResource(R.drawable.roundedbuttongreen);
                buttonStatus = true;

                vibrate.setEnabled(false);
                muteSound.setEnabled(false);

                startTimeChecking();
            }else {
                mainButton.setText("OFF");
                mainButton.setBackgroundResource(R.drawable.roundedbuttonred);

                vibrate.setEnabled(true);
                muteSound.setEnabled(true);
            }
        }
    }

    public void infoButton (View view) {
        InfoDialog infoDialog = new InfoDialog();
        infoDialog.show(getSupportFragmentManager(), "info dialog");
    }


    private void startTimeChecking () {
        if (stopExecution) {
            stopExecution = false;
        }
        startService(new Intent(this, CheckTime.class));
    }

    private void stopTimeChecking () {
        stopExecution = true;
        stopService(new Intent(this, CheckTime.class));
    }
}