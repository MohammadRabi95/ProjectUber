package com.example.projectuber;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.projectuber.Models.Rides;
import com.example.projectuber.Utils.RideSession;

public class AcceptRideActivity extends AppCompatActivity {

    private Rides ride;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept_ride);
        ride = RideSession.getRideModel(this);


    }
}