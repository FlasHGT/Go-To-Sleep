package com.example.gotosleep;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import com.example.gotosleep.ui.main.SectionsPagerAdapter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

public class MainActivity extends AppCompatActivity {

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String BUTTON_STATUS = "buttonStatus";

    public static int controlValue = 0; // (controlValue <= 1) no tasks running or one task running, (controlValue > 1) too many tasks are running, destroy instances until there is only one left.
    public static boolean stopExecution = false;

    public static Button mainButton;

    public static final String VIBRATE_SWITCH = "vibrateSwitch";
    public static final String MUTE_SOUND_SWITCH = "muteSoundSwitch";

    public static Switch muteSound, vibrate;

    public boolean buttonStatus = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        mainButton = findViewById(R.id.mainButton);

        loadData();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();

        saveData();
    }

    public void mainButtonBehaviour (View view) {
        if (buttonStatus) {
            if (view != null) {
                mainButton.setText("TURN ON");
                mainButton.setBackgroundResource(R.drawable.roundedbuttonred);
                buttonStatus = false;

                vibrate.setEnabled(true);
                muteSound.setEnabled(true);

                stopTimeChecking();
            }
            else {
                mainButton.setText("TURN OFF");
                mainButton.setBackgroundResource(R.drawable.roundedbuttongreen);

                vibrate.setEnabled(false);
                muteSound.setEnabled(false);
            }
        }
        else {
            if (view != null) {
                mainButton.setText("TURN OFF");
                mainButton.setBackgroundResource(R.drawable.roundedbuttongreen);
                buttonStatus = true;

                vibrate.setEnabled(false);
                muteSound.setEnabled(false);

                startTimeChecking();
            }
            else {
                mainButton.setText("TURN ON");
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


    public void saveData () {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean(BUTTON_STATUS, buttonStatus).apply();
    }

    public void loadData () {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

        buttonStatus = sharedPreferences.getBoolean(BUTTON_STATUS, true);
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
