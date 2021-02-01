package com.vcrow.rides;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.vcrow.rides.Auth.SignInActivity;
import com.vcrow.rides.Driver.AcceptRideActivity;
import com.vcrow.rides.Maps.PassengerMapsActivity;
import com.vcrow.rides.Passenger.MyRideActivity;
import com.vcrow.rides.Utils.AppHelper;
import com.vcrow.rides.Utils.CurrentUser;
import com.vcrow.rides.Utils.RideSession;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            if (AppHelper.isInternetAvailable(SplashActivity.this)) {
                if (AppHelper.isUserAvailable()) {
                    if (CurrentUser.IsDriver(SplashActivity.this)) {
                        if (RideSession.IsRideAccepted(SplashActivity.this) ||
                                RideSession.IsRideInProgress(SplashActivity.this)) {
                            startActivity(new Intent(SplashActivity.this, AcceptRideActivity.class));
                        } else {
                            startActivity(new Intent(SplashActivity.this, SelectModeActivity.class));
                        }
                        finish();
                    } else {
                        if (RideSession.IsGettingRide(this)) {
                            startActivity(new Intent(SplashActivity.this, PassengerMapsActivity.class));
                            finish();
                        } else if (RideSession.IsRideInProgress(SplashActivity.this)) {
                            startActivity(new Intent(SplashActivity.this, MyRideActivity.class));
                            finish();
                        } else {
                            startActivity(new Intent(SplashActivity.this, SelectModeActivity.class));
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
        }, 1000);
    }
}