package com.example.projectuber.Passenger;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.example.projectuber.DatabaseCalls;
import com.example.projectuber.Interfaces.ResponseInterface;
import com.example.projectuber.Models.RideProgress;
import com.example.projectuber.R;
import com.example.projectuber.Utils.AppHelper;
import com.example.projectuber.Utils.RideSession;

import dmax.dialog.SpotsDialog;

public class MyRideActivity extends AppCompatActivity {

    private String id = "";
    private boolean isRideStarted = false;
    private boolean isRideCompleted = false;
    private RideProgress progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_ridectivity);

        id = getIntent().getStringExtra("id");
        getRideData();
        Toast.makeText(this, "Work in Progress", Toast.LENGTH_SHORT).show();


    }

    private void setLayoutAccordingly() {
        if (isRideStarted) {

        } else if (isRideCompleted) {

        }
    }
    private void getRideData() {
        DatabaseCalls.isRideStartedCall(this, id, new ResponseInterface() {
            @Override
            public void onResponse(Object... params) {
                    isRideStarted = true;
                    progress = (RideProgress) params[0];
             }
            @Override
            public void onError(String error) {
            }
        });

        DatabaseCalls.isRideCompletedCall(this, id, new ResponseInterface() {
            @Override
            public void onResponse(Object... params) {
                if ((boolean) params[0]) {
                    isRideStarted = false;
                    isRideCompleted = true;
                }
            }
            @Override
            public void onError(String error) {
            }
        });
    }
}