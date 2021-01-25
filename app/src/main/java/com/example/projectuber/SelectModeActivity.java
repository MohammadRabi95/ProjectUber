package com.example.projectuber;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.projectuber.Auth.SignInActivity;
import com.example.projectuber.CompletedRides.CompletedRidesActivity;
import com.example.projectuber.Maps.DriverMapsActivity;
import com.example.projectuber.Maps.PassengerMapsActivity;
import com.example.projectuber.Utils.CurrentUser;
import com.example.projectuber.Utils.RideSession;

public class SelectModeActivity extends AppCompatActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_mode);

        toolbar = findViewById(R.id.toolbar_select);
        setSupportActionBar(toolbar);
        DatabaseCalls.getCurrentUserCall(this);

        findViewById(R.id.drive).setOnClickListener(view -> {
            CurrentUser.setDrivingMode(this, true);
            startActivity(new Intent(this, DriverMapsActivity.class));
            finish();
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
}