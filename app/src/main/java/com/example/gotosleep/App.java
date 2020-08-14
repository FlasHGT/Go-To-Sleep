package com.example.gotosleep;

import android.app.Application;
import android.content.Intent;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public void startTimeChecking () {
        startService(new Intent(this, CheckTime.class));
    }

    public void stopTimeChecking () {
        stopService(new Intent(this, CheckTime.class));
    }
}