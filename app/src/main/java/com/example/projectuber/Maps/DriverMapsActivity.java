package com.example.projectuber.Maps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
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
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projectuber.Driver.AcceptRideActivity;
import com.example.projectuber.Adapters.RidesAdapter;
import com.example.projectuber.DatabaseCalls;
import com.example.projectuber.Interfaces.ResponseInterface;
import com.example.projectuber.Interfaces.RidesCallback;
import com.example.projectuber.Models.Ride;
import com.example.projectuber.Models.RideProgress;
import com.example.projectuber.R;
import com.example.projectuber.Utils.AppHelper;
import com.example.projectuber.Utils.RideSession;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.ButtCap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.projectuber.Utils.AppHelper.decodePoly;
import static com.example.projectuber.Utils.Constants.polyLineWidth;

public class DriverMapsActivity extends FragmentActivity implements OnMapReadyCallback, RidesCallback {

    public static final int PERMISSIONS_REQUEST_ENABLE_GPS = 111;
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 222;
    public static final int ERROR_DIALOG_REQUEST = 333;
    private static final String TAG = "DriverMapsActivity";
    private GoogleMap mMap;
    private RecyclerView recyclerView;
    private TextView textView;
    private CardView cardView;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private boolean mLocationPermissionGranted = false;
    private API api;
    private List<LatLng> latLngList;
    private PolylineOptions polylineOptions;
    private List<Legs> legsList;
    private Polyline polyline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        recyclerView = findViewById(R.id.map_recyclerView);
        textView = findViewById(R.id.tv_map);
        cardView = findViewById(R.id.cv_map);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        Retrofit retrofit = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl("https://maps.googleapis.com/").build();
        api = retrofit.create(API.class);
        latLngList = new ArrayList<>();
        legsList = new ArrayList<>();

    }

    @Override
    protected void onStart() {
        super.onStart();
        getRides();
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
        mMap.setTrafficEnabled(true);
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
            LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.addMarker(new MarkerOptions().position(loc).title("Current Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
        }
    }

    @Override
    public void onRideAccepted(Ride ride) {
        DatabaseCalls.setRideAcceptCall(DriverMapsActivity.this, ride, new ResponseInterface() {
            @Override
            public void onResponse(Object... params) {
                if ((boolean) params[0]) {
                    RideSession.setRideAccepted(DriverMapsActivity.this, true);
                    RideSession.setRideModel(DriverMapsActivity.this, (RideProgress) params[1]);
                    startActivity(new Intent(DriverMapsActivity.this, AcceptRideActivity.class));
                } else {
                    AppHelper.showSnackBar(findViewById(android.R.id.content), getString(R.string.somthing_wrong));
                }
            }

            @Override
            public void onError(String error) {
                AppHelper.showSnackBar(findViewById(android.R.id.content), error);
            }
        });

    }

    @Override
    public void onRideSelected(String pickupLat, String pickupLng,
                               String dropOffLat, String dropOffLng,
                               String pickupLocation, String dropOffLocation) {
        if (mLocationPermissionGranted) {
            mMap.clear();
            LatLng pickUpLatLng = new LatLng(Double.parseDouble(pickupLat), Double.parseDouble(pickupLng));
            LatLng dropOffLatLng = new LatLng(Double.parseDouble(dropOffLat), Double.parseDouble(dropOffLng));
            retrofitCall(pickupLat + "," + pickupLng, dropOffLat + "," + dropOffLng,
                    pickUpLatLng, dropOffLatLng);

            mMap.addMarker(new MarkerOptions().position(pickUpLatLng).title(pickupLocation));
            mMap.addMarker(new MarkerOptions().position(dropOffLatLng).title(dropOffLocation));

        }
    }

    private void setAdapter(List<Ride> list) {
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(new RidesAdapter(this, list, this));
    }

    private void getRides() {
        DatabaseCalls.getRidesCall(this, new ResponseInterface() {
            @Override
            public void onResponse(Object... params) {
                setAdapter((List<Ride>) params[0]);
            }

            @Override
            public void onError(String error) {
                AppHelper.showSnackBar(findViewById(android.R.id.content), error);
            }
        });
    }

    private void retrofitCall(String origin, String destination, LatLng orig, LatLng dest) {
        api.getDirection("driving", "less_driving",
                origin, destination, getString(R.string.google_maps_key))
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Result>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onSuccess(Result value) {
                        List<Route> routeList = value.getRoutes();
                        for (Route route : routeList) {
                            String polyline = route.getOverViewPolyline().getPoints();
                            latLngList.addAll(decodePoly(polyline));
                            legsList.addAll(route.getLegs());
                        }
                        if (polyline != null) {
                            polyline.remove();
                        }
                        float distance = legsList.get(0).getDistance().getValue() / 1000;
                        float duration = legsList.get(0).getDuration().getValue() / 60;
                        String s = "Ride Distance: " + legsList.get(0).getDistance().getText()
                                + "\nRide Duration: " + legsList.get(0).getDuration().getText()
                                + "\nEstimated Fair: " + AppHelper.calculateFairs(distance, duration)
                                + " $";
                        polylineOptions = new PolylineOptions();
                        polylineOptions.color(ContextCompat.getColor(getApplicationContext(),
                                R.color.black));
                        polylineOptions.width(polyLineWidth);
                        polylineOptions.startCap(new ButtCap());
                        polylineOptions.jointType(JointType.ROUND);
                        polylineOptions.addAll(latLngList);
                        polyline = mMap.addPolyline(polylineOptions);
                        cardView.setVisibility(View.VISIBLE);
                        textView.setText(s);
                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        builder.include(orig);
                        builder.include(dest);
                        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 85));

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: ", e);
                    }
                });
    }


}