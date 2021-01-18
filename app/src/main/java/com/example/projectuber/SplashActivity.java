package com.example.projectuber;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.example.projectuber.Auth.SignInActivity;
import com.example.projectuber.Utils.RideSession;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Handler handler =new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (RideSession.IsRideAccepted(SplashActivity.this)){

                }
                if (RideSession.IsRideInProgress(SplashActivity.this)) {

                }
                Intent intent=new Intent(SplashActivity.this, SignInActivity.class);
                startActivity(intent);
                finish();
            }
        },2000);
    }
}