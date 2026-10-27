package com.hostel.manager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            SessionManager session = new SessionManager(SplashActivity.this);
            if (session.isLoggedIn()) {
                if ("admin".equalsIgnoreCase(session.getRole())) {
                    startActivity(new Intent(SplashActivity.this, AdminMainActivity.class));
                } else {
                    startActivity(new Intent(SplashActivity.this, StudentMainActivity.class));
                }
            } else {
                startActivity(new Intent(SplashActivity.this, RoleSelectActivity.class));
            }
            finish();
        }, 1500);
    }
}
