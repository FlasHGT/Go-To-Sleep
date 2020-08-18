package com.example.gotosleep;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;

@RequiresApi(api = Build.VERSION_CODES.O)
public class SettingsActivity extends AppCompatActivity {

    private MainActivity mainActivity = new MainActivity();
    private Switch vibrate, muteSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        vibrate = findViewById(R.id.vibrateSwitch);
        muteSound = findViewById(R.id.muteSoundSwitch);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadData();
    }

    public void saveData () {
        SharedPreferences sharedPreferences = getSharedPreferences(mainActivity.SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean(mainActivity.MUTE_SOUND_SWITCH, muteSound.isChecked());
        editor.putBoolean(mainActivity.VIBRATE_SWTICH, vibrate.isChecked());

        editor.apply();
    }

    public void loadData () {
        SharedPreferences sharedPreferences = getSharedPreferences(mainActivity.SHARED_PREFS, MODE_PRIVATE);

        vibrate.setChecked(sharedPreferences.getBoolean(mainActivity.VIBRATE_SWTICH, false));
        muteSound.setChecked(sharedPreferences.getBoolean(mainActivity.MUTE_SOUND_SWITCH, false));
    }

    public void goToMainActivity (View view) {
        saveData();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finishAffinity();
    }

    @Override
    protected void onStop() {
        super.onStop();

        saveData();
    }
}