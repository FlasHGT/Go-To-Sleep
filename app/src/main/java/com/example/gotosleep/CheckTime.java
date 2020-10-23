package com.example.gotosleep;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Build;
import android.os.IBinder;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import android.view.Display;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.gotosleep.ui.main.TimeFragment;

import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
public class CheckTime extends Service {

    private static final int NOTIF_ID = 1;
    private static final String NOTIF_CHANNEL_ID = "Main";

    private TimeFragment timeFragment = new TimeFragment();
    private Display display;

    private int brightness = 0;
    private int startBrightness = 0;

    private int secondsToDelay = 0;

    private int currentHour = 0;
    private int currentMin = 0;

    private Vibrator vibrator;
    private AudioManager audioManager;

    private boolean vibratorSwitched = false;
    private boolean muteSoundSwitched = false;
    private boolean screenFlashSwitched = false;

    private ScheduledExecutorService scheduleTaskExecutor = Executors.newScheduledThreadPool(2);
    private ScheduledFuture<?> scheduledFuture;
    private ScheduledFuture<?> activeFuture;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            secondsToDelay = 60 - OffsetDateTime.now().getSecond();
        }else {
            String currentSeconds = new SimpleDateFormat("ss", Locale.getDefault()).format(new Date());
            secondsToDelay = 60 - Integer.parseInt(currentSeconds);
        }

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        if (MainActivity.controlValue != 0) {
            MainActivity.controlValue++;
        }

        scheduledFuture = scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.R)
            public void run() {
                if (MainActivity.stopExecution) {
                    MainActivity.controlValue--;
                    scheduledFuture.cancel(true);

                    if (muteSoundSwitched) {
                        muteAllSound(false);
                    }

                    if (MainActivity.controlValue == 0) {
                        return;
                    }
                }

                if (MainActivity.controlValue > 1) {
                    MainActivity.controlValue--;
                    scheduledFuture.cancel(true);
                    return;
                }
                else if (MainActivity.controlValue == 1) {
                    scheduledFuture.cancel(true);
                    startNewExecution();
                    return;
                }

                if (MainActivity.controlValue == 0) {
                    MainActivity.controlValue++;
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    currentHour = OffsetDateTime.now().getHour();
                    currentMin = OffsetDateTime.now().getMinute();
                }else {
                    String currentTime = new SimpleDateFormat("hh:mm", Locale.getDefault()).format(new Date());
                    String[] separated = currentTime.split(":");

                    currentHour = Integer.parseInt(separated[0]);
                    currentMin = Integer.parseInt(separated[1]);
                }

                display = getDisplay();

                if (checkTimeInRange()) {
                    if (muteSoundSwitched) {
                        muteAllSound(true);
                    }

                    if (display.getState() == Display.STATE_ON) { // 1 - screen off, 2 - screen on
                        try {
                            startBrightness = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
                        } catch (Settings.SettingNotFoundException e) {
                            e.printStackTrace();
                        }

                        activeBehaviour();
                    }

                }else {
                    if (muteSoundSwitched) {
                        muteAllSound(false);
                    }
                }

                Log.d("delay", "" + secondsToDelay);

            }
        }, 0, secondsToDelay, TimeUnit.SECONDS);

        createNotificationChannel();
        startForeground();

        return super.onStartCommand(intent, flags, startId);
    }

    public void startNewExecution() {
        secondsToDelay = 60;

        scheduledFuture = scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
            public void run() {
                if (MainActivity.stopExecution) {
                    scheduledFuture.cancel(true);
                    MainActivity.controlValue = 0;
                    return;
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    currentHour = OffsetDateTime.now().getHour();
                    currentMin = OffsetDateTime.now().getMinute();
                }else {
                    String currentTime = new SimpleDateFormat("hh:mm", Locale.getDefault()).format(new Date());
                    String[] separated = currentTime.split(":");

                    currentHour = Integer.parseInt(separated[0]);
                    currentMin = Integer.parseInt(separated[1]);
                }

                if (checkTimeInRange()) {
                    if (muteSoundSwitched) {
                        muteAllSound(true);
                    }

                    if (display.getState() == Display.STATE_ON) { // 1 - screen off, 2 - screen on
                        activeBehaviour();
                    }
                }else {
                    if (muteSoundSwitched) {
                        muteAllSound(false);
                    }
                }

                Log.d("delay", "" + secondsToDelay);

            }
        }, 0, secondsToDelay, TimeUnit.SECONDS);
    }

    public void loadData () {
        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFS, MODE_PRIVATE);

        timeFragment.t1Hour = sharedPreferences.getInt(TimeFragment.T1HOUR, 0);
        timeFragment.t1Minute = sharedPreferences.getInt(TimeFragment.T1MINUTE, 0);
        timeFragment.t2Hour = sharedPreferences.getInt(TimeFragment.T2HOUR, 6);
        timeFragment.t2Minute = sharedPreferences.getInt(TimeFragment.T2MINUTE, 0);
        vibratorSwitched = sharedPreferences.getBoolean(MainActivity.VIBRATE_SWITCH, false);
        muteSoundSwitched = sharedPreferences.getBoolean(MainActivity.MUTE_SOUND_SWITCH, false);
        screenFlashSwitched = sharedPreferences.getBoolean(MainActivity.SCREEN_FLASH_SWITCH, false);
    }

    private void activeBehaviour() {
        if (activeFuture == null) {
            activeFuture = scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    if (checkTimeInRange()) {
                        if (display.getState() == Display.STATE_OFF && MainActivity.stopExecution) {
                            activeFuture.cancel(true);
                            activeFuture = null;
                            return;
                        }

                        if (MainActivity.stopExecution) {
                            android.provider.Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, startBrightness);
                        }

                        if (muteSoundSwitched) {
                            if (MainActivity.stopExecution) {
                                muteAllSound(false);
                            }else {
                                muteAllSound(true);
                            }
                        }

                        if (display.getState() == Display.STATE_ON && !MainActivity.stopExecution) {
                            if (vibratorSwitched) {
                                Log.d("123", "vibrate");
                                vibrator.vibrate(1000);
                            }

                            if (screenFlashSwitched) {
                                if (brightness == 0) {
                                    brightness = 255;
                                }else {
                                    brightness = 0;
                                }

                                android.provider.Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, brightness);
                            }
                        }
                    }else {
                        android.provider.Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, startBrightness);
                        activeFuture.cancel(true);
                        activeFuture = null;
                    }
                }
            },0,2, TimeUnit.SECONDS);
        }
    }

    private void muteAllSound(boolean muteSound) {
        if (muteSound) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                audioManager.adjustStreamVolume(AudioManager.STREAM_NOTIFICATION, AudioManager.ADJUST_MUTE, 0);
                audioManager.adjustStreamVolume(AudioManager.STREAM_ALARM, AudioManager.ADJUST_MUTE, 0);
                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_MUTE, 0);
                audioManager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_MUTE, 0);
                audioManager.adjustStreamVolume(AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_MUTE, 0);
            } else {
                audioManager.setStreamMute(AudioManager.STREAM_NOTIFICATION, true);
                audioManager.setStreamMute(AudioManager.STREAM_ALARM, true);
                audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
                audioManager.setStreamMute(AudioManager.STREAM_RING, true);
                audioManager.setStreamMute(AudioManager.STREAM_SYSTEM, true);
            }
        }else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (audioManager.isStreamMute(AudioManager.STREAM_MUSIC)) {
                    audioManager.adjustStreamVolume(AudioManager.STREAM_NOTIFICATION, AudioManager.ADJUST_UNMUTE, 0);
                    audioManager.adjustStreamVolume(AudioManager.STREAM_ALARM, AudioManager.ADJUST_UNMUTE, 0);
                    audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_UNMUTE,0);
                    audioManager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_UNMUTE, 0);
                    audioManager.adjustStreamVolume(AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_UNMUTE, 0);
                }
            } else {
                if (audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) == 0) {
                    audioManager.setStreamMute(AudioManager.STREAM_NOTIFICATION, false);
                    audioManager.setStreamMute(AudioManager.STREAM_ALARM, false);
                    audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
                    audioManager.setStreamMute(AudioManager.STREAM_RING, false);
                    audioManager.setStreamMute(AudioManager.STREAM_SYSTEM, false);
                }
            }
        }

    }

    private boolean checkTimeInRange () {
        loadData();

        if ((timeFragment.t1Hour <= currentHour || timeFragment.t2Hour < timeFragment.t1Hour)
                && timeFragment.t2Hour >= currentHour
                && (timeFragment.t1Minute <= currentMin || (timeFragment.t1Hour < currentHour || timeFragment.t2Hour < timeFragment.t1Hour))
                && (timeFragment.t2Minute >= currentMin || timeFragment.t2Hour > currentHour))
        {
            return true;
        }else {
            return false;
        }
    }

    private void startForeground() {
        Intent notificationIntent = new Intent(this, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        startForeground(NOTIF_ID, new NotificationCompat.Builder(this,
                NOTIF_CHANNEL_ID) // don't forget create a notification channel first
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setColor(ContextCompat.getColor(this, R.color.black))
                .setContentText("Checking time")
                .setContentIntent(pendingIntent)
                .build());
    }

    private void createNotificationChannel () {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Check time";
            int importance = NotificationManager.IMPORTANCE_MIN;
            NotificationChannel channel = new NotificationChannel(NOTIF_CHANNEL_ID, name, importance);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}

