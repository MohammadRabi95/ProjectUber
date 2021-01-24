package com.example.projectuber.Passenger;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.example.projectuber.R;
import com.example.projectuber.Utils.AppHelper;

import dmax.dialog.SpotsDialog;

public class MyRideActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_ridectivity);

        SpotsDialog dialog = AppHelper.showLoadingDialog(this);
        dialog.show();
        Toast.makeText(this, "Work in Progress", Toast.LENGTH_SHORT).show();

    }
}