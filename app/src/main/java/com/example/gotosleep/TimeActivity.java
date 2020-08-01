package com.example.gotosleep;

import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

public class TimeActivity extends AppCompatActivity {

    private TextView startTime, endTime = null;
    private int t1Hour, t1Minute, t2Hour, t2Minute = 0;

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
                        TimeActivity.this,
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
    }

    public void goToSettingsActivity (View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

}

