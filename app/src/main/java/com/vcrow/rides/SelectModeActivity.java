package com.vcrow.rides;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.vcrow.rides.Auth.SignInActivity;
import com.vcrow.rides.CompletedRides.CompletedRidesActivity;
import com.vcrow.rides.Driver.DriverCarDetailsActivity;
import com.vcrow.rides.Interfaces.ResponseInterface;
import com.vcrow.rides.Maps.DriverMapsActivity;
import com.vcrow.rides.Maps.PassengerMapsActivity;
import com.vcrow.rides.Utils.AppHelper;
import com.vcrow.rides.Utils.CurrentUser;
import com.vcrow.rides.Utils.RideSession;

public class SelectModeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_mode);

        Toolbar toolbar = findViewById(R.id.toolbar_select);
        setSupportActionBar(toolbar);
        DatabaseCalls.getCurrentUserCall(this);

        findViewById(R.id.drive).setOnClickListener(view -> {
            checkIsDriverCarRegistered();
        });

        findViewById(R.id.ride).setOnClickListener(view -> {
            CurrentUser.setDrivingMode(this, false);
            startActivity(new Intent(this, PassengerMapsActivity.class));
            finish();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.completed_ride:
                startActivity(new Intent(this, CompletedRidesActivity.class));
                break;
            case R.id.rate_app:
                rateApp();
                break;
            case R.id.contact_us:
                contactUs();
                break;
            case R.id.logout:
                RideSession.resetRideSession(this);
                CurrentUser.signOut();
                startActivity(new Intent(this, SignInActivity.class));
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void contactUs() {

    }

    private void rateApp() {

    }

    private void checkIsDriverCarRegistered() {
        DatabaseCalls.isUserDriver(this, new ResponseInterface() {
            @Override
            public void onResponse(Object... params) {
                if ((boolean) params[0]) {
                    startActivity(new Intent(SelectModeActivity.this, DriverMapsActivity.class));
                } else {
                    startActivity(new Intent(SelectModeActivity.this, DriverCarDetailsActivity.class));
                }
                finish();
            }

            @Override
            public void onError(String error) {
                AppHelper.showSnackBar(findViewById(android.R.id.content), error);
            }
        });
    }
}