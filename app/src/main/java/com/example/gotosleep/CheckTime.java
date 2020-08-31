package com.example.gotosleep;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Build;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;
import android.view.Display;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

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

    private MainActivity mainActivity = new MainActivity();
    private Display display;

    private int secondsToDelay = 0;

    private int currentHour = 0;
    private int currentMin = 0;

    private Vibrator vibrator;
    private AudioManager audioManager;
    private boolean vibratorSwitched = false;
    private boolean muteSoundSwitched = false;

    private ScheduledExecutorService scheduleTaskExecutor = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> scheduledFuture;
    private ScheduledFuture<?> vibrateFuture;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            secondsToDelay = 60 - OffsetDateTime.now().getSecond();
        }else {
            String currentSeconds = new SimpleDateFormat("ss", Locale.getDefault()).format(new Date());
            secondsToDelay = 60 - Integer.parseInt(currentSeconds);
        }

        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        if (MainActivity.controlValue != 0) {
            MainActivity.controlValue++;
        }

        scheduledFuture = scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
            public void run() {
                if (MainActivity.stopExecution) {
                    MainActivity.controlValue--;
                    scheduledFuture.cancel(true);

                    if (MainActivity.controlValue == 0) {
                        MainActivity.stopExecution = false;
                    }

                    return;
                }

                if (MainActivity.controlValue > 1) {
                    MainActivity.controlValue--;
                    scheduledFuture.cancel(true);
                    return;
                } else if (MainActivity.controlValue == 1) {
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

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    display = getDisplay();
                }

                if (checkTimeInRange()) {  // 1 - screen off, 2 - screen on
                    if (muteSoundSwitched) {
                        muteSound();
                    }

                    if (display.getState() == Display.STATE_ON) {
                        if (vibratorSwitched) {
                            startVibration();
                        }
                    }
                }else {
                    unmuteSound();
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

                if (checkTimeInRange()) {  // 1 - screen off, 2 - screen on
                    if (muteSoundSwitched) {
                        muteSound();
                    }

                    if (display.getState() == Display.STATE_ON) {
                        if (vibratorSwitched) {
                            startVibration();
                        }
                    }
                }else {
                    if (muteSoundSwitched) {
                        unmuteSound();
                    }
                }

                Log.d("delay", "" + secondsToDelay);

            }
        }, 0, secondsToDelay, TimeUnit.SECONDS);
    }

    public void loadData () {
        SharedPreferences sharedPreferences = getSharedPreferences(mainActivity.SHARED_PREFS, MODE_PRIVATE);

        mainActivity.t1Hour = sharedPreferences.getInt(mainActivity.T1HOUR, 0);
        mainActivity.t1Minute = sharedPreferences.getInt(mainActivity.T1MINUTE, 0);
        mainActivity.t2Hour = sharedPreferences.getInt(mainActivity.T2HOUR, 6);
        mainActivity.t2Minute = sharedPreferences.getInt(mainActivity.T2MINUTE, 0);
        vibratorSwitched = sharedPreferences.getBoolean(mainActivity.VIBRATE_SWTICH, false);
        muteSoundSwitched = sharedPreferences.getBoolean(mainActivity.MUTE_SOUND_SWITCH, false);
    }

    private void startVibration() {
        if (vibrateFuture == null) {
            vibrateFuture = scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    if ((checkTimeInRange() && display.getState() == Display.STATE_ON) && !MainActivity.stopExecution && vibratorSwitched) {
                        vibrator.vibrate(1000);
                    }else {
                        vibrateFuture.cancel(true);
                        vibrateFuture = null;
                    }

                    if (!muteSoundSwitched) {
                        unmuteSound();
                    }
                }
            },0,2, TimeUnit.SECONDS);
        }
    }

    private void muteSound () {
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
    }

    private void unmuteSound () {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            audioManager.adjustStreamVolume(AudioManager.STREAM_NOTIFICATION, AudioManager.ADJUST_UNMUTE, 0);
            audioManager.adjustStreamVolume(AudioManager.STREAM_ALARM, AudioManager.ADJUST_UNMUTE, 0);
            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_UNMUTE,0);
            audioManager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_UNMUTE, 0);
            audioManager.adjustStreamVolume(AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_UNMUTE, 0);
        } else {
            audioManager.setStreamMute(AudioManager.STREAM_NOTIFICATION, false);
            audioManager.setStreamMute(AudioManager.STREAM_ALARM, false);
            audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
            audioManager.setStreamMute(AudioManager.STREAM_RING, false);
            audioManager.setStreamMute(AudioManager.STREAM_SYSTEM, false);
        }
    }

    private boolean checkTimeInRange () {
        loadData();

        if (mainActivity.t1Hour <= currentHour && mainActivity.t2Hour >= currentHour && mainActivity.t1Minute <= currentMin &&
           (mainActivity.t2Minute >= currentMin || (mainActivity.t2Minute < currentMin && mainActivity.t2Hour > currentHour) || (mainActivity.t2Minute > currentMin && mainActivity.t2Hour == currentHour))
        ) {
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
                .setSmallIcon(R.drawable.ic_transparent)
                .setContentText("Checking time")
                .setContentIntent(pendingIntent)
                .build());
    }

    private void createNotificationChannel () {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Registering time";
            int importance = NotificationManager.IMPORTANCE_MIN;
            NotificationChannel channel = new NotificationChannel(NOTIF_CHANNEL_ID, name, importance);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}

