package com.example.projectuber.Maps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projectuber.DatabaseCalls;
import com.example.projectuber.Interfaces.ResponseInterface;
import com.example.projectuber.Models.Ride;
import com.example.projectuber.Passenger.GetRideActivity;
import com.example.projectuber.R;
import com.example.projectuber.Utils.AppHelper;
import com.example.projectuber.Utils.CurrentUser;
import com.example.projectuber.Utils.RideSession;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.ButtCap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.projectuber.DatabaseCalls.rideRef;
import static com.example.projectuber.Utils.AppHelper.decodePoly;

public class PassengerMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    public static final int PERMISSIONS_REQUEST_ENABLE_GPS = 111;
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 222;
    public static final int ERROR_DIALOG_REQUEST = 333;
    private static final String TAG = "PassengerMapsActivity";
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private boolean mLocationPermissionGranted = false;
    private API api;
    private List<LatLng> latLngList;
    private PolylineOptions polylineOptions;
    private List<Legs> legsList;
    private LatLng pick_ltln, drop_ltln;
    private Ride ride;
    private TextView pickup_et, dropOff_et;
    private AutocompleteSupportFragment pick_frag, drop_frag;
    private Button next_btn;
    private PlacesClient placesClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_maps);
        pickup_et = findViewById(R.id.pick_et);
        dropOff_et = findViewById(R.id.drop_et);
        next_btn = findViewById(R.id.btn_next);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        initializeMyPlaces();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        Retrofit retrofit = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl("https://maps.googleapis.com/").build();
        api = retrofit.create(API.class);
        latLngList = new ArrayList<>();
        legsList = new ArrayList<>();
        if (RideSession.IsGettingRide(this)) {
            ride = RideSession.getUserRideModel(this);
            next_btn.setVisibility(View.GONE);
            pickup_et.setText(ride.getPickup_location());
            dropOff_et.setText(ride.getDropOff_location());
            pick_frag.setText(ride.getPickup_location());
            drop_frag.setText(ride.getDropOff_location());
            String origin = ride.getPickup_latitude() + "," + ride.getPickup_longitude();
            String destination = ride.getDropOff_latitude() + "," + ride.getDropOff_longitude();
            LatLng orig = new LatLng(Double.parseDouble(ride.getPickup_latitude()),
                    Double.parseDouble(ride.getPickup_longitude()));
            LatLng dest = new LatLng(Double.parseDouble(ride.getDropOff_latitude()),
                    Double.parseDouble(ride.getDropOff_longitude()));
            retrofitCall(origin, destination, orig, dest, ride.getPickup_location(), ride.getDropOff_location());
        } else {
            next_btn.setOnClickListener(view -> {
                if (validate()) {
                    String origin = pick_ltln.latitude + "," + pick_ltln.longitude;
                    String dest = drop_ltln.latitude + "," + drop_ltln.longitude;
                    retrofitCall(origin, dest, pick_ltln, drop_ltln, pickup_et.getText().toString(),
                            dropOff_et.getText().toString());
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
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
            Log.e(TAG, "getCurrentLocation: ", e);
            AppHelper.showSnackBar(findViewById(android.R.id.content), e.getMessage());
        });
    }

    private void setMapData(Location location) {
        if (mLocationPermissionGranted) {
            LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.addMarker(new MarkerOptions().position(loc).title("Current Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
        }
    }

    private void retrofitCall(String origin, String destination, LatLng orig, LatLng dest,
                              String pickup_location, String dropOff_location) {
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

                        float distance = legsList.get(0).getDistance().getValue() / 1000;
                        float duration = legsList.get(0).getDuration().getValue() / 60;
                        String s = legsList.get(0).getDistance().getText();
                        String s1 = legsList.get(0).getDuration().getText();
                        String s2 = AppHelper.calculateFairs(distance, duration) + " $";

                        showTripDetailsBottomSheet(s, s1, s2, pickup_location, dropOff_location,
                                orig.latitude, orig.longitude, dest.latitude, dest.longitude);
                        polylineOptions = new PolylineOptions();
                        polylineOptions.color(ContextCompat.getColor(getApplicationContext(),
                                R.color.black));
                        polylineOptions.width(20);
                        polylineOptions.startCap(new ButtCap());
                        polylineOptions.jointType(JointType.ROUND);
                        polylineOptions.addAll(latLngList);
                        mMap.addPolyline(polylineOptions);
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

    private void showTripDetailsBottomSheet(String distance, String duration, String price,
                                            String pickup, String dropoff, double p_lat,
                                            double p_lon, double d_lat, double d_lon) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = LayoutInflater.from(this).inflate(R.layout.ride_details_bottomsheet, null);
        TextView pickup_tv = bottomSheetView.findViewById(R.id.pick_btm);
        TextView dropoff_tv = bottomSheetView.findViewById(R.id.drop_btm);
        TextView duration_tv = bottomSheetView.findViewById(R.id.duration_btm);
        TextView distance_tv = bottomSheetView.findViewById(R.id.distance_btm);
        TextView price_tv = bottomSheetView.findViewById(R.id.price_btm);
        Button btn_getRide = bottomSheetView.findViewById(R.id.btn_getride);

        distance_tv.setText(distance);
        duration_tv.setText(duration);
        price_tv.setText(price);
        pickup_tv.setText(pickup);
        dropoff_tv.setText(dropoff);

        if (RideSession.IsGettingRide(this)) {
            DatabaseCalls.isRideAcceptedCall(ride.getId(), new ResponseInterface() {
                @Override
                public void onResponse(Object... params) {
                    if ((boolean) params[0]) {
                        startActivity(new Intent(PassengerMapsActivity.this, GetRideActivity.class));
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
        } else {
            btn_getRide.setOnClickListener(view -> {
                bottomSheetDialog.show();
                Ride ride = new Ride();

                ride.setDistance(distance);
                ride.setDuration(duration);
                ride.setName(CurrentUser.getName());
                ride.setPrice(price);
                ride.setPickup_location(pickup);
                ride.setDropOff_location(dropoff);
                ride.setId(CurrentUser.getUserId());
                ride.setPhone(CurrentUser.getUsersPhoneNum());
                ride.setPickup_latitude(p_lat + "");
                ride.setPickup_longitude(p_lon + "");
                ride.setDropOff_latitude(d_lat + "");
                ride.setDropOff_longitude(d_lon + "");

                String id = rideRef.push().getKey();
                ride.setId(id);

                DatabaseCalls.setRidesCall(ride, new ResponseInterface() {
                    @Override
                    public void onResponse(Object... params) {
                        if ((boolean) params[0]) {
                            RideSession.setGettingRide(PassengerMapsActivity.this, true);
                            RideSession.setUserRideModel(PassengerMapsActivity.this, ride);
                            DatabaseCalls.isRideAcceptedCall(id, new ResponseInterface() {
                                @Override
                                public void onResponse(Object... params) {
                                    if ((boolean) params[0]) {
                                        startActivity(new Intent(PassengerMapsActivity.this, GetRideActivity.class));
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

            });
        }
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.setCancelable(false);
        bottomSheetDialog.setCanceledOnTouchOutside(false);
        bottomSheetDialog.show();
    }

    private boolean validate() {
        if (pickup_et.getText().toString().isEmpty()) {
            pickup_et.setError(getString(R.string.cannot_be_empty));
            pickup_et.requestFocus();
            return false;
        } else if (dropOff_et.getText().toString().isEmpty()) {
            dropOff_et.setError(getString(R.string.cannot_be_empty));
            dropOff_et.requestFocus();
            return false;
        }
        return true;
    }

    private void initializeMyPlaces() {
        pick_frag = (AutocompleteSupportFragment) getSupportFragmentManager().
                findFragmentById(R.id.pick_fragment);
        drop_frag = (AutocompleteSupportFragment) getSupportFragmentManager().
                findFragmentById(R.id.drop_fragment);
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
        }
        placesClient = Places.createClient(PassengerMapsActivity.this);

        pick_frag.setCountries("US");
        pick_frag.setHint("Pickup");
        pick_frag.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS));

        drop_frag.setCountries("US");
        drop_frag.setHint("Drop Off");
        drop_frag.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS));

        pick_frag.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
            pick_ltln = place.getLatLng();
            pick_frag.setText(place.getAddress());
            pickup_et.setText(place.getAddress());
            }

            @Override
            public void onError(@NonNull Status status) {
                Log.e(TAG, "onError: PickUp " + status);
                AppHelper.showSnackBar(findViewById(android.R.id.content), status.getStatusMessage());
            }
        });

        drop_frag.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                drop_ltln = place.getLatLng();
                drop_frag.setText(place.getAddress());
                dropOff_et.setText(place.getAddress());
            }

            @Override
            public void onError(@NonNull Status status) {
                Log.e(TAG, "onError: DropOff " + status);
                AppHelper.showSnackBar(findViewById(android.R.id.content), status.getStatusMessage());
            }
        });
    }
}