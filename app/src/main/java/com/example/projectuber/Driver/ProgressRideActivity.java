package com.example.projectuber.Driver;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.example.projectuber.R;

public class ProgressRideActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_ride);

        Toast.makeText(this, "Work in Progress", Toast.LENGTH_SHORT).show();
    }
}