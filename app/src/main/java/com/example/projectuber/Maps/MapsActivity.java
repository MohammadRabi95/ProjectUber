package com.example.projectuber.Maps;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.projectuber.Adapters.RidesAdapter;
import com.example.projectuber.Interfaces.RidesCallback;
import com.example.projectuber.Models.Rides;
import com.example.projectuber.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, RidesCallback {

    private ImageView imageView_search;
    private EditText editText_search;
    public static final int PERMISSIONS_REQUEST_ENABLE_GPS = 111;
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 222;
    public static final int ERROR_DIALOG_REQUEST = 333;
    private static final String TAG = "MapsActivity";
    private GoogleMap mMap;
    private RecyclerView recyclerView;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private boolean mLocationPermissionGranted = false;
    //private boolean search_et_is_visible=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        editText_search=findViewById(R.id.et_search_MapActivity);
        imageView_search=findViewById(R.id.iv_search_mapActivity);
        recyclerView = findViewById(R.id.map_recyclerView);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);


        imageView_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* if(search_et_is_visible)
                {
                    //invoke search method  here,
                    editText_search.setVisibility(View.INVISIBLE);
                    search_et_is_visible=false;
                }
                else {
                    editText_search.setVisibility(View.VISIBLE);
                    search_et_is_visible=true;
                    editText_search.requestFocus();
                }*/
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        setAdapter(dummyList());
        if (checkMapServices()) {
            if (mLocationPermissionGranted) {
                getCurrentLocation();
            } else {
                getLocationPermission();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    private boolean checkMapServices() {
        if (isServicesOK()) {
            return isGPSEnabled();
        }
        return false;
    }

    private void buildAlertMessageNoGps() {
        final androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public boolean isGPSEnabled() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }

    private void getLocationPermission() {

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            getCurrentLocation();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    public boolean isServicesOK() {
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);

        if (available == ConnectionResult.SUCCESS) {
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Log.d(TAG, "isServicesOK: an error came but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
                getCurrentLocation();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: called.");
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ENABLE_GPS: {
                if (mLocationPermissionGranted) {
                    getCurrentLocation();
                } else {
                    getLocationPermission();
                }
            }
        }
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        final Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(location -> {
            if (location != null) {
               setMapData(location);
            }
        }).addOnFailureListener(e -> {

        });
    }

    private void setMapData(Location location) {
        if (mLocationPermissionGranted) {
            LatLng sydney = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.addMarker(new MarkerOptions().position(sydney).title("Current Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        }
    }

    @Override
    public void onRideAccepted(Rides rides) {
        // navigate to ride start activity
    }

    @Override
    public void onRideSelected(String pickupLat, String pickupLng,
                               String dropOffLat, String dropOffLng,
                               String pickupLocation, String dropOffLocation) {
        if (mLocationPermissionGranted) {
            // create route on map
            LatLng pickUpLatLng = new LatLng(Double.parseDouble(pickupLat), Double.parseDouble(pickupLng));
            LatLng dropOffLatLng = new LatLng(Double.parseDouble(dropOffLat), Double.parseDouble(dropOffLng));
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(pickUpLatLng).title(pickupLocation));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(pickUpLatLng));
            mMap.animateCamera(CameraUpdateFactory.zoomIn());
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);

        }
    }

    private void setAdapter(List<Rides> list){
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(new RidesAdapter(this, list, this));
    }

    private List<Rides> dummyList() {
        List<Rides> list = new ArrayList<>();
        list.add(new Rides("1","1","Ali",getString(R.string.dummy_loc),
                "31.520370", "74.358749",
                "+923364982522",getString(R.string.dummy_loc),
                "74.358749", "31.520375"));
        list.add(new Rides("2","2","Hamza",getString(R.string.dummy_loc),
                "32.520370", "74.358749",
                "+923364982522",getString(R.string.dummy_loc),
                "74.358749", "31.520375"));
        list.add(new Rides("3","3","Raza",getString(R.string.dummy_loc),
                "34.520370", "74.358749",
                "+923364982522",getString(R.string.dummy_loc),
                "74.358749", "31.520375"));

        return list;

    }

}