package com.flash.gotosleep;

import android.app.NotificationManager;
import android.content.res.Resources;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import com.flash.gotosleep.ui.main.SectionsPagerAdapter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String BUTTON_STATUS = "buttonStatus";
    public static final String ACTIVE_PRESET ="activePreset";

    public int currentActivePreset = 1;

    public static int controlValue = 0; // (controlValue <= 1) no tasks running or one task running, (controlValue > 1) too many tasks are running, destroy instances until there is only one left.
    public static boolean stopExecution = true;

    public static Button mainButton;

    public static final String VIBRATE_SWITCH_1 = "vibrateSwitch1";
    public static final String MUTE_SOUND_SWITCH_1 = "muteSoundSwitch1";
    public static final String SCREEN_FLASH_SWITCH_1 = "screenFlashSwitch1";

    public static final String VIBRATE_SWITCH_2 = "vibrateSwitch2";
    public static final String MUTE_SOUND_SWITCH_2 = "muteSoundSwitch2";
    public static final String SCREEN_FLASH_SWITCH_2 = "screenFlashSwitch2";

    public static final String VIBRATE_SWITCH_3 = "vibrateSwitch3";
    public static final String MUTE_SOUND_SWITCH_3 = "muteSoundSwitch3";
    public static final String SCREEN_FLASH_SWITCH_3 = "screenFlashSwitch3";

    public static String vibrateSwitchTemp = "VIBRATE_SWITCH_";
    public static String muteSoundSwitchTemp = "MUTE_SOUND_SWITCH_";
    public static String screenFlashSwitchTemp = "SCREEN_FLASH_SWITCH_";

    public static Switch muteSound, vibrate, screenFlash;

    public static ImageView preset1, preset2, preset3;

    public boolean buttonStatus = false;

    private ScheduledExecutorService scheduleTaskExecutor = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> notificationFuture;

    private Resources resources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resources = getResources();

        checkIfNotificationIsRunning();

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        mainButton = findViewById(R.id.mainButton);

        loadData();
    }

    @Override
    protected void onStop() {
        super.onStop();

        saveData();
    }

    public void mainButtonBehaviour (View view) {
        if (view != null) {
            SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
            currentActivePreset = sharedPreferences.getInt(ACTIVE_PRESET, 1);
        }

        if (buttonStatus) {
            if (view != null) {
                mainButton.setText("TURN ON");
                mainButton.setBackgroundResource(R.drawable.roundedbuttonred);
                buttonStatus = false;

                enableSwitches();
                enablePresetButtons();

                stopTimeChecking();
            }
            else {
                mainButton.setText("TURN OFF");
                mainButton.setBackgroundResource(R.drawable.roundedbuttongreen);

                disableSwitches();
                disablePresetButtons();
            }
        }
        else {
            if (view != null) {
                mainButton.setText("TURN OFF");
                mainButton.setBackgroundResource(R.drawable.roundedbuttongreen);
                buttonStatus = true;

                disableSwitches();
                disablePresetButtons();

                startTimeChecking();
            }
            else {
                mainButton.setText("TURN ON");
                mainButton.setBackgroundResource(R.drawable.roundedbuttonred);

                enableSwitches();
                enablePresetButtons();
            }
        }

        if (view != null) {
            saveData();
        }
    }

    public void saveData () {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean(BUTTON_STATUS, buttonStatus).apply();
    }

    public void loadData () {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

        buttonStatus = sharedPreferences.getBoolean(BUTTON_STATUS, false);
    }

    private void disablePresetButtons () {
        if(preset1 != null && resources != null) {
            switch (currentActivePreset) {
                case 1:
                    preset1.setColorFilter(resources.getColor(R.color.greenGrey));
                    preset2.setColorFilter(resources.getColor(R.color.grey));
                    preset3.setColorFilter(resources.getColor(R.color.grey));
                    break;
                case 2:
                    preset2.setColorFilter(resources.getColor(R.color.greenGrey));
                    preset1.setColorFilter(resources.getColor(R.color.grey));
                    preset3.setColorFilter(resources.getColor(R.color.grey));
                    break;
                case 3:
                    preset3.setColorFilter(resources.getColor(R.color.greenGrey));
                    preset1.setColorFilter(resources.getColor(R.color.grey));
                    preset2.setColorFilter(resources.getColor(R.color.grey));
                    break;
            }
        }
    }

    private void enablePresetButtons () {
        if (preset1 != null && resources != null) {
            switch (currentActivePreset) {
                case 1:
                    preset1.setColorFilter(resources.getColor(R.color.green));
                    preset2.setColorFilter(resources.getColor(R.color.white));
                    preset3.setColorFilter(resources.getColor(R.color.white));
                    break;
                case 2:
                    preset2.setColorFilter(resources.getColor(R.color.green));
                    preset1.setColorFilter(resources.getColor(R.color.white));
                    preset3.setColorFilter(resources.getColor(R.color.white));
                    break;
                case 3:
                    preset3.setColorFilter(resources.getColor(R.color.green));
                    preset1.setColorFilter(resources.getColor(R.color.white));
                    preset2.setColorFilter(resources.getColor(R.color.white));
                    break;
            }
        }
    }

    private void enableSwitches () {
        vibrate.setEnabled(true);
        muteSound.setEnabled(true);
        screenFlash.setEnabled(true);
    }

    private void disableSwitches () {
        vibrate.setEnabled(false);
        muteSound.setEnabled(false);
        screenFlash.setEnabled(false);
    }

    private void checkIfNotificationIsRunning() {
        if (notificationFuture == null) {
            notificationFuture = scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        loadData();

                        NotificationManager notificationManager = getSystemService(NotificationManager.class);

                        if(notificationManager.getActiveNotifications().length == 0 && buttonStatus) {
                            stopTimeChecking();
                            startTimeChecking();
                        }
                    }
                }
            },0, 12, TimeUnit.HOURS);
        }
    }

    private void startTimeChecking () {
        stopExecution = false;
        startService(new Intent(this, CheckTime.class));
    }

    private void stopTimeChecking () {
        stopExecution = true;
        stopService(new Intent(this, CheckTime.class));
    }
}
