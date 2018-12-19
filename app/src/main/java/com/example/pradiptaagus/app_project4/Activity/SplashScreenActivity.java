package com.example.pradiptaagus.app_project4.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.example.pradiptaagus.app_project4.R;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkSharedPreference();
                finish();
            }
        }, 2000);
    }

    private void checkSharedPreference() {
        SharedPreferences userPreference = this.getSharedPreferences("user", Context.MODE_PRIVATE);
        try {
            String token = userPreference.getString("token", "missing");
            if (token == "missing") {
                startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
            } else {
                startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
            }
        } catch (Exception e) {
            startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
        }

    }
}
