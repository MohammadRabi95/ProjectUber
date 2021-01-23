package com.example.projectuber.Driver;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.projectuber.DatabaseCalls;
import com.example.projectuber.Interfaces.ResponseInterface;
import com.example.projectuber.Models.RideProgress;
import com.example.projectuber.R;
import com.example.projectuber.Utils.AppHelper;
import com.example.projectuber.Utils.RideSession;
import com.github.clans.fab.FloatingActionButton;

public class AcceptRideActivity extends AppCompatActivity implements View.OnClickListener {

    private RideProgress ride;
    private TextView name_tv, pickup_tv, drop_tv,
            distance_tv, time_tv, price_tv;
    private FloatingActionButton call_btn, direc_btn;
    private Button start_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept_ride);

        if (RideSession.IsRideAccepted(this)) {
            ride = RideSession.getRideModel(this);
        } else {
            finish();
        }
        if (ride != null) {
            setData();
        } else {
            AppHelper.showSnackBar(findViewById(android.R.id.content),
                    getString(R.string.somthing_wrong));
        }


    }

    private void setData() {
        name_tv = findViewById(R.id.pass_nme);
        pickup_tv = findViewById(R.id.pick_loc);
        drop_tv = findViewById(R.id.drop_loc);
        distance_tv = findViewById(R.id.dist);
        time_tv = findViewById(R.id.time);
        price_tv = findViewById(R.id.price);
        call_btn = findViewById(R.id.fab_call);
        direc_btn = findViewById(R.id.fab_directions);
        start_btn = findViewById(R.id.btn_rideStart);

        name_tv.setText(ride.getPassengerName());
        pickup_tv.setText(ride.getPickup_location());
        drop_tv.setText(ride.getDropOff_location());
        distance_tv.setText(ride.getDistance());
        time_tv.setText(ride.getDuration());
        price_tv.setText(ride.getPrice() + " $");

        call_btn.setOnClickListener(this);
        direc_btn.setOnClickListener(this);
        start_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_rideStart:
                onRideStarted();
                break;
            case R.id.fab_call:
                startActivity(new Intent(Intent.ACTION_DIAL,
                        Uri.parse("tel:" + ride.getPassengerPhone())));
                break;
            case R.id.fab_directions:
                openGoogleMapsForRoute();
                break;
        }
    }

    private void openGoogleMapsForRoute() {

    }

    private void onRideStarted() {
        DatabaseCalls.setRideProgressCall(ride.getId(), new ResponseInterface() {
            @Override
            public void onResponse(Object... params) {
                if ((boolean) params[0]) {
                    RideSession.setRideInProgress(AcceptRideActivity.this, true);
                    ride.setRideStarted(true);
                    RideSession.setRideModel(AcceptRideActivity.this, ride);
                    startActivity(new Intent(AcceptRideActivity.this, ProgressRideActivity.class));
                    finish();
                } else {
                    AppHelper.showSnackBar(findViewById(android.R.id.content),
                            getString(R.string.somthing_wrong));
                }
            }
            @Override
            public void onError(String error) {
                AppHelper.showSnackBar(findViewById(android.R.id.content), error);
            }
        });
    }
}