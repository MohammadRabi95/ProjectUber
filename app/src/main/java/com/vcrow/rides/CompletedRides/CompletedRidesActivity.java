package com.vcrow.rides.CompletedRides;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.vcrow.rides.Adapters.CompletedRidesTabAdapter;
import com.vcrow.rides.R;
import com.google.android.material.tabs.TabLayout;

public class CompletedRidesActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private CompletedRidesTabAdapter completedRidesTabAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed_rides);

        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.viewPager);

        completedRidesTabAdapter = new CompletedRidesTabAdapter(getSupportFragmentManager(), 1);
        completedRidesTabAdapter.addFragment(new AsDriverFragment(), "As Driver");
        completedRidesTabAdapter.addFragment(new AsPassengerFragment(), "As Passenger");

        viewPager.setAdapter(completedRidesTabAdapter);
        tabLayout.setupWithViewPager(viewPager);

    }
}