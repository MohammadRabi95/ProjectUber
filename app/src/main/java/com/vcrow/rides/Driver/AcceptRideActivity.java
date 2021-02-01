package com.vcrow.rides.Driver;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.vcrow.rides.DatabaseCalls;
import com.vcrow.rides.Interfaces.ResponseInterface;
import com.vcrow.rides.Models.RideProgress;
import com.vcrow.rides.R;
import com.vcrow.rides.Utils.AppHelper;
import com.vcrow.rides.Utils.RideSession;
import com.github.clans.fab.FloatingActionButton;

public class AcceptRideActivity extends AppCompatActivity implements View.OnClickListener {

    private RideProgress ride;
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
        TextView name_tv = findViewById(R.id.pass_nme);
        TextView pickup_tv = findViewById(R.id.pick_loc);
        TextView drop_tv = findViewById(R.id.drop_loc);
        TextView distance_tv = findViewById(R.id.dist);
        TextView time_tv = findViewById(R.id.time);
        TextView price_tv = findViewById(R.id.price);
        FloatingActionButton call_btn = findViewById(R.id.fab_call);
        FloatingActionButton direc_btn = findViewById(R.id.fab_directions);
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

        if (RideSession.IsRideInProgress(this)) {
            isRideStarted();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_rideStart:
                if (RideSession.IsRideInProgress(this)) {
                    onRideFinished();
                } else if (RideSession.IsRideAccepted(this)) {
                    onRideStarted();
                }
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

    private void onRideFinished() {
        DatabaseCalls.setRideCompletedCall(this, ride, new ResponseInterface() {
            @Override
            public void onResponse(Object... params) {
                if (!((boolean) params[0])) {
                    AppHelper.showSnackBar(findViewById(android.R.id.content),
                            getString(R.string.somthing_wrong));
                }
                RideSession.resetRideSession(AcceptRideActivity.this);
                startActivity(new Intent(AcceptRideActivity.this, DriverPaymentActivity.class)
                        .putExtra("model", ride));
                finish();
            }

            @Override
            public void onError(String error) {
                AppHelper.showSnackBar(findViewById(android.R.id.content), error);
            }
        });
    }

    private void openGoogleMapsForRoute() {
        String origin = ride.getPickup_latitude() + "," + ride.getPickup_longitude();
        String destination = ride.getDropOff_latitude() + "," + ride.getDropOff_longitude();
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?saddr=" + origin + "&daddr=" + destination));
        startActivity(intent);
    }

    private void isRideStarted() {
        DatabaseCalls.isRideStartedCall(this, ride.getId(), new ResponseInterface() {
            @Override
            public void onResponse(Object... params) {
                RideProgress rideProgress = (RideProgress) params[0];
                if (rideProgress.isRideStarted()) {
                    start_btn.setText("End Ride");
                }
            }

            @Override
            public void onError(String error) {
                AppHelper.showSnackBar(findViewById(android.R.id.content), error);
            }
        });
    }

    private void onRideStarted() {
        DatabaseCalls.setRideProgressCall(this, ride.getId(), new ResponseInterface() {
            @Override
            public void onResponse(Object... params) {
                if ((boolean) params[0]) {
                    RideSession.setRideAccepted(AcceptRideActivity.this, false);
                    RideSession.setRideInProgress(AcceptRideActivity.this, true);
                    ride.setRideStarted(true);
                    RideSession.setRideModel(AcceptRideActivity.this, ride);
                    start_btn.setText("End Ride");
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