package com.example.rdvmanager;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

    private static int visibleActivityCount = 0;

    @Override
    protected void onStart() {
        super.onStart();
        visibleActivityCount++;
        if (visibleActivityCount == 1) {
            startService(new Intent(this, BackgroundMusicService.class));
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        visibleActivityCount--;
        if (visibleActivityCount == 0) {
            stopService(new Intent(this, BackgroundMusicService.class));
        }
    }
}
