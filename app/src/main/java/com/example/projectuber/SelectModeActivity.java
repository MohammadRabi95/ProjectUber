package com.example.projectuber;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.projectuber.Maps.DriverMapsActivity;
import com.example.projectuber.Maps.PassengerMapsActivity;
import com.example.projectuber.Utils.CurrentUser;

public class SelectModeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_mode);

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
}