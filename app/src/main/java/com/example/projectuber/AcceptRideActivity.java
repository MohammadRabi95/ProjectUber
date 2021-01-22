package com.example.projectuber;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.projectuber.Models.Ride;
import com.example.projectuber.Utils.RideSession;

public class AcceptRideActivity extends AppCompatActivity {

    private Ride ride;
    private TextView textView_from,textView_to,textView_clint_name,textView_duration,textView_fair;
    private ImageView imageView_back;
    private Button button_start_ride;
    private Rides ride;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept_ride);
        ride = RideSession.getRideModel(this);

        textView_from=findViewById(R.id.tv_ride_from_acceptRideActivity);
        textView_to=findViewById(R.id.tv_ride_to_acceptRideActivity);
        textView_clint_name=findViewById(R.id.tv_input_captain_name_acceptRideActivity);
        textView_duration=findViewById(R.id.tv_input_ride_duration_acceptRideActivity);
        textView_fair=findViewById(R.id.tv_input_ride_fair_acceptRideActivity);
        imageView_back=findViewById(R.id.iv_back_acceptRideActivity);
        button_start_ride=findViewById(R.id.btn_start_ride__acceptRideActivity);

        imageView_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }
}