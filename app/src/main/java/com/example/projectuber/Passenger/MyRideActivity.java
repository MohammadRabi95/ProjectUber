package com.example.projectuber.Passenger;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projectuber.DatabaseCalls;
import com.example.projectuber.Interfaces.ResponseInterface;
import com.example.projectuber.Models.Car;
import com.example.projectuber.Models.CompletedRide;
import com.example.projectuber.Models.RideProgress;
import com.example.projectuber.R;
import com.example.projectuber.Utils.AppHelper;
import com.example.projectuber.Utils.RideSession;

import dmax.dialog.SpotsDialog;

import static com.example.projectuber.Utils.Constants.completedRideKey;

public class MyRideActivity extends AppCompatActivity {

    private String id = "";
    private TextView status, d_name, car_details, pick_loc, drop_loc, distance, price;
    private Button btn_cnl;
    private RideProgress progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_ridectivity);

        id = getIntent().getStringExtra("id");

        status = findViewById(R.id.status);
        d_name = findViewById(R.id.pass_nme);
        car_details = findViewById(R.id.car_det);
        pick_loc = findViewById(R.id.pick_loc);
        drop_loc = findViewById(R.id.drop_loc);
        distance = findViewById(R.id.dist);
        price = findViewById(R.id.price);

        btn_cnl = findViewById(R.id.btn_cnl);

        getRideData();

        btn_cnl.setOnClickListener(view -> {
            // cancel ride
        });
    }

    private void setData(RideProgress rideProgress, Car car) {
        if (rideProgress.isRideStarted()) {
            status.setText("Ride Started");
        } else {
            status.setText("Ride Accepted");
        }
        d_name.setText(rideProgress.getDriverName());
        String d = car.getModel() + " " + car.getColor()
                + " " + car.getCompany() + " " +
                car.getName() + " (" + car.getNumber() + ")";
        car_details.setText(d);
        pick_loc.setText(rideProgress.getPickup_location());
        drop_loc.setText(rideProgress.getDropOff_location());
        distance.setText(rideProgress.getDistance());
        price.setText(rideProgress.getPrice());

    }

    private void getRideData() {
        DatabaseCalls.isRideStartedCall(this, id, new ResponseInterface() {
            @Override
            public void onResponse(Object... params) {
                btn_cnl.setClickable(false);
                progress = (RideProgress) params[0];
                DatabaseCalls.getCarInfo(MyRideActivity.this, progress.getDriverId(),
                        new ResponseInterface() {
                            @Override
                            public void onResponse(Object... params) {
                                if ((boolean) params[0]) {
                                    Car car = (Car) params[1];
                                    setData(progress, car);
                                }
                            }
                            @Override
                            public void onError(String error) {
                                AppHelper.showSnackBar(findViewById(android.R.id.content), error);
                            }
                        });
            }

            @Override
            public void onError(String error) {
                AppHelper.showSnackBar(findViewById(android.R.id.content), error);
            }
        });

        DatabaseCalls.isRideCompletedCall(this, id, new ResponseInterface() {
            @Override
            public void onResponse(Object... params) {
                if ((boolean) params[0]) {
                    CompletedRide completedRide = (CompletedRide) params[1];
                    startActivity(new Intent(MyRideActivity.this,
                            PassengerPaymentActivity.class).putExtra(completedRideKey, completedRide));
                    finish();
                }
            }

            @Override
            public void onError(String error) {
                AppHelper.showSnackBar(findViewById(android.R.id.content), error);
            }
        });
    }
}