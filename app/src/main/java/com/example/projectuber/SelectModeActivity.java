package com.example.projectuber;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.projectuber.Maps.DriverMapsActivity;
import com.example.projectuber.Maps.PassengerMapsActivity;

public class SelectModeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_mode);

        findViewById(R.id.drive).setOnClickListener(view -> {
            startActivity(new Intent(this, DriverMapsActivity.class));
            finish();
        });

        findViewById(R.id.ride).setOnClickListener(view -> {
            startActivity(new Intent(this, PassengerMapsActivity.class));
            finish();
        });
    }
}