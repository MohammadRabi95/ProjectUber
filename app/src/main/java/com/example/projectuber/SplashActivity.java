package com.example.projectuber;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.example.projectuber.Auth.SignInActivity;
import com.example.projectuber.Maps.MapsActivity;
import com.example.projectuber.Utils.AppHelper;
import com.example.projectuber.Utils.CurrentUser;
import com.example.projectuber.Utils.RideSession;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Handler handler =new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            if (AppHelper.isInternetAvailable(SplashActivity.this)) {
                if (AppHelper.isUserAvailable()) {
                    if (CurrentUser.IsDriver(SplashActivity.this)) {
                        if (!RideSession.IsRideAccepted(SplashActivity.this)) {
                            //TODO: remove "!" from above condition
                            startActivity(new Intent(SplashActivity.this, AcceptRideActivity.class));
                            finish();
                        } else if (RideSession.IsRideInProgress(SplashActivity.this)) {
                                //TODO: navigate to ride progress driver activity
                        } else {
                            startActivity(new Intent(SplashActivity.this, MapsActivity.class));
                            finish();
                        }
                    } else {
                        if (!RideSession.IsRideAccepted(SplashActivity.this)) {
                            //TODO: remove "!" from above condition
                            // navigate to passenger side ride accepted activity
                            startActivity(new Intent(SplashActivity.this, AcceptRideActivity.class));
                            finish();
                        } else if (RideSession.IsRideInProgress(SplashActivity.this)) {
                            //TODO: navigate to ride progress passenger activity
                        } else {
                            startActivity(new Intent(SplashActivity.this, GetRideActivity.class));
                            finish();
                        }
                    }
                } else {
                    Intent intent = new Intent(SplashActivity.this, SignInActivity.class);
                    startActivity(intent);
                    finish();
                }
            } else {
                AppHelper.showSnackBar(findViewById(android.R.id.content), "No Internet Connection");
            }
        },2000);
    }
}