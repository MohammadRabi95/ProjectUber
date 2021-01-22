package com.example.projectuber;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.projectuber.Interfaces.ResponseInterface;
import com.example.projectuber.Models.CompletedRide;
import com.example.projectuber.Models.Ride;
import com.example.projectuber.Models.RideProgress;
import com.example.projectuber.Models.User;
import com.example.projectuber.Utils.AppHelper;
import com.example.projectuber.Utils.CurrentUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DatabaseCalls {

    private static final String TAG = "DatabaseCalls";

    private static final String Users = "Users";
    private static final String Rides = "Rides";
    private static final String ProgressRides = "ProgressRides";
    private static final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference(Users);
    private static final DatabaseReference rideRef = FirebaseDatabase.getInstance().getReference(Rides);
    private static final DatabaseReference progressRidesRef = FirebaseDatabase.getInstance().getReference(ProgressRides);


    public static void setRidesCall(Ride ride, ResponseInterface responseInterface) {
        String id = rideRef.push().getKey();
        ride.setId(id);
        rideRef.child(id).setValue(ride).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                responseInterface.onResponse(true);
            } else {
                responseInterface.onResponse(false);
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "onFailure: setRides ", e);
            responseInterface.onError(e.getMessage());
        });
    }

    public static void getRidesCall(ResponseInterface responseInterface) {
        List<Ride> list = new ArrayList<>();
        rideRef.keepSynced(true);
        rideRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        if (dataSnapshot.exists()) {
                            Ride ride = dataSnapshot.getValue(Ride.class);
                            if (ride != null) {
                                list.add(ride);
                            }
                        }
                    }
                    responseInterface.onResponse(list);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: getRides ", error.toException());
                responseInterface.onError(error.getDetails());
            }
        });
    }

    public static void setUserCall(User user, ResponseInterface responseInterface) {
        userRef.child(user.getId()).setValue(user).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                responseInterface.onResponse(true);
            } else {
                responseInterface.onResponse(false);
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "onFailure: setUser ", e);
            responseInterface.onError(e.getMessage());
        });
    }

    public static void isUserSavedCall(String id, ResponseInterface responseInterface) {
        rideRef.keepSynced(true);
        Query query = userRef.orderByChild("id").equalTo(id);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                responseInterface.onResponse(snapshot.exists());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: isUserSaved ", error.toException());
                responseInterface.onError(error.getDetails());
            }
        });
    }

    public static void removeFromRideProgressCall(RideProgress rideProgress,
                                                ResponseInterface responseInterface){
        progressRidesRef.child(rideProgress.getId()).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                responseInterface.onResponse(true);
            } else {
                responseInterface.onResponse(false);
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "onFailure: removeFromRideAcceptCall ", e);
            responseInterface.onError(e.getMessage());
        });

    }

    public static void setRideCompletedCall(RideProgress rideProgress, ResponseInterface responseInterface) {
        removeFromRideProgressCall(rideProgress, new ResponseInterface() {
            @Override
            public void onResponse(Object... params) {
                if ((boolean) params[0]) {
                    CompletedRide completedRide = new CompletedRide();
                    completedRide.setDriverId(rideProgress.getDriverId());
                    completedRide.setDriverName(rideProgress.getDriverName());
                    completedRide.setDriverPhone(rideProgress.getDriverPhone());
                    completedRide.setId(rideProgress.getId());
                    completedRide.setPassengerId(rideProgress.getPassengerId());
                    completedRide.setPassengerName(rideProgress.getPassengerName());
                    completedRide.setPassengerPhone(rideProgress.getPassengerPhone());
                    completedRide.setPickup_location(rideProgress.getPickup_location());
                    completedRide.setDropOff_location(rideProgress.getDropOff_location());
                    completedRide.setPickup_latitude(rideProgress.getPickup_latitude());
                    completedRide.setDropOff_latitude(rideProgress.getDropOff_latitude());
                    completedRide.setPickup_longitude(rideProgress.getPickup_longitude());
                    completedRide.setDropOff_longitude(rideProgress.getDropOff_longitude());
                    completedRide.setRideAcceptedTimeStamp(rideProgress.getRideAcceptedTimeStamp());
                    completedRide.setPickupTimeStamp(rideProgress.getPickupTimeStamp());
                    completedRide.setDropOffTimeStamp(AppHelper.getTimeStamp());
                    progressRidesRef.child(rideProgress.getId()).setValue(completedRide)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    responseInterface.onResponse(true);
                                } else {
                                    responseInterface.onResponse(false);
                                }
                            }).addOnFailureListener(e -> {
                        Log.e(TAG, "onFailure: setRideCompleteCall ", e);
                        responseInterface.onError(e.getMessage());
                    });
                } else {
                    Log.d(TAG, "onResponse: setRideCompleteCall Response from removeFromRideAcceptCall is false");
                }
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "onError: setRideCompleteCall Error from removeFromRideAcceptCall " + error);
            }
        });
    }

    public static void setRideProgressCall(Ride ride, boolean isRideStarted,
                                         ResponseInterface responseInterface) {
        removeFromRideCall(ride, new ResponseInterface() {
            @Override
            public void onResponse(Object... params) {
                if ((boolean) params[0]) {
                    RideProgress progress = new RideProgress();
                    progress.setDriverId(CurrentUser.getUserId());
                    progress.setDriverName(CurrentUser.getName());
                    progress.setDriverPhone(CurrentUser.getUsersPhoneNum());
                    progress.setId(ride.getId());
                    progress.setPassengerId(ride.getUserId());
                    progress.setPassengerName(ride.getName());
                    progress.setPassengerPhone(ride.getPhone());
                    progress.setPickup_location(ride.getPickup_location());
                    progress.setDropOff_location(ride.getDropOff_location());
                    progress.setPickup_latitude(ride.getPickup_latitude());
                    progress.setDropOff_latitude(ride.getDropOff_latitude());
                    progress.setPickup_longitude(ride.getPickup_longitude());
                    progress.setDropOff_longitude(ride.getDropOff_longitude());
                    progress.setRideAcceptedTimeStamp(AppHelper.getTimeStamp());
                    progress.setRideStarted(isRideStarted);
                    progress.setPickupTimeStamp("");
                    progressRidesRef.child(ride.getId()).setValue(progress)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    responseInterface.onResponse(true);
                                } else {
                                    responseInterface.onResponse(false);
                                }
                            }).addOnFailureListener(e -> {
                        Log.e(TAG, "onFailure: setRideAcceptCall ", e);
                        responseInterface.onError(e.getMessage());
                    });
                } else {
                    Log.d(TAG, "onResponse: setRideAcceptCall Response from removeRideCall is false");
                }
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "onError: setRideAcceptCall Error from removeRideCall " + error);
            }
        });
    }

    private static void removeFromRideCall(Ride ride, ResponseInterface responseInterface) {
        rideRef.child(ride.getId()).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                responseInterface.onResponse(true);
            } else {
                responseInterface.onResponse(false);
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "onFailure: removeRideCall ", e);
            responseInterface.onError(e.getMessage());
        });
    }
}
