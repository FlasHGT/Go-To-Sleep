package com.example.gotosleep;

import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
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

                                if (hour < 12) {
                                    mainActivity.am_PM1 = "AM";
                                }else {
                                    mainActivity.am_PM1 = "PM";
                                }

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

                                if (hour < 12) {
                                    mainActivity.am_PM2 = "AM";
                                }else {
                                    mainActivity.am_PM2 = "PM";
                                }

                                Calendar calendar = Calendar.getInstance();

                                calendar.set(0, 0, 0, mainActivity.t2Hour, mainActivity.t2Minute);

                                endTime.setText(DateFormat.format("hh:mm aa", calendar));

                                Log.d("lol", "" + mainActivity.t2Hour);
                        }
                        }, 12, 0, false
                );

                timePickerDialog.updateTime(mainActivity.t2Hour, mainActivity.t2Minute);
                timePickerDialog.show();
            }
        });
    }



    public void goToSettingsActivity (View view) {
        mainActivity.saveData();

        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

}

