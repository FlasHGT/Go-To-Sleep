package com.example.gotosleep;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@RequiresApi(api = Build.VERSION_CODES.O)
public class CheckTime extends Service {

    private static final int NOTIF_ID = 1;
    private static final String NOTIF_CHANNEL_ID = "Main";

    private int secondsToDelay = 0;

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
    private String currentTime = "";

    private ScheduledExecutorService scheduleTaskExecutor = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> scheduledFuture;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId){

        secondsToDelay = 60 - OffsetDateTime.now().getSecond();

        Log.d("123", "" + MainActivity.controlValue);

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

                Log.d("123", "main: " + MainActivity.controlValue);

                Log.d("delay", "" + secondsToDelay);

                //do stuff

                currentTime = formatter.format(OffsetDateTime.now());

            }
        }, 0, secondsToDelay, TimeUnit.SECONDS);

        Log.d("123", "" + MainActivity.controlValue);

        createNotificationChannel();
        startForeground();

        return super.onStartCommand(intent, flags, startId);
    }

    public void startNewExecution() {
        secondsToDelay = 60;

        scheduledFuture = scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void run() {
                if (MainActivity.stopExecution) {
                    scheduledFuture.cancel(true);
                    MainActivity.controlValue = 0;
                    return;
                }

                //do stuff
                currentTime = formatter.format(OffsetDateTime.now());

                Log.d("delay", "" + secondsToDelay);
            }
        }, 0, secondsToDelay, TimeUnit.SECONDS);
    }

    private void startForeground() {
        Intent notificationIntent = new Intent(this, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        startForeground(NOTIF_ID, new NotificationCompat.Builder(this,
                NOTIF_CHANNEL_ID) // don't forget create a notification channel first
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Service is running background")
                .setContentIntent(pendingIntent)
                .build());
    }

    private void createNotificationChannel () {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Register Time";
            int importance = NotificationManager.IMPORTANCE_MIN;
            NotificationChannel channel = new NotificationChannel(NOTIF_CHANNEL_ID, name, importance);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}

