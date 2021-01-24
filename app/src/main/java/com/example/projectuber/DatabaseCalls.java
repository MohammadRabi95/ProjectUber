package com.example.projectuber;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.projectuber.Interfaces.ResponseInterface;
import com.example.projectuber.Models.CompletedRide;
import com.example.projectuber.Models.Ride;
import com.example.projectuber.Models.RideProgress;
import com.example.projectuber.Models.User;
import com.example.projectuber.Utils.AppHelper;
import com.example.projectuber.Utils.CurrentUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class DatabaseCalls {

    private static final String TAG = "DatabaseCalls";

    private static final String Users = "Users";
    private static final String Rides = "Rides";
    private static final String ProgressRides = "ProgressRides";
    private static final String CompletedRides = "CompletedRides";
    public static final DatabaseReference rideRef = FirebaseDatabase.getInstance().getReference(Rides);
    private static final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference(Users);
    private static final DatabaseReference progressRidesRef = FirebaseDatabase.getInstance().getReference(ProgressRides);
    private static final DatabaseReference completedRidesRef = FirebaseDatabase.getInstance().getReference(CompletedRides);


    public static void setRidesCall(Context context, Ride ride, ResponseInterface responseInterface) {
        SpotsDialog dialog = AppHelper.showLoadingDialog(context);
        dialog.show();
        rideRef.child(ride.getId()).setValue(ride).addOnCompleteListener(task -> {
            responseInterface.onResponse(task.isSuccessful());
            dialog.dismiss();
        }).addOnFailureListener(e -> {
            dialog.dismiss();
            Log.e(TAG, "onFailure: setRides ", e);
            responseInterface.onError(e.getMessage());
        });
    }

    public static void getRidesCall(Context context, ResponseInterface responseInterface) {
        SpotsDialog dialog = AppHelper.showLoadingDialog(context);
        dialog.show();
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
                    dialog.dismiss();
                    responseInterface.onResponse(list);
                } else {
                    dialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
                Log.e(TAG, "onCancelled: getRides ", error.toException());
                responseInterface.onError(error.getDetails());
            }
        });
    }

    public static void setUserCall(Context context, User user, ResponseInterface responseInterface) {
        SpotsDialog dialog = AppHelper.showLoadingDialog(context);
        dialog.show();
        userRef.child(user.getId()).setValue(user).addOnCompleteListener(task -> {
            responseInterface.onResponse(task.isSuccessful());
            dialog.dismiss();
        }).addOnFailureListener(e -> {
            Log.e(TAG, "onFailure: setUser ", e);
            responseInterface.onError(e.getMessage());
            dialog.dismiss();
        });
    }

    public static void getCurrentUserCall(Context context) {
        userRef.child(CurrentUser.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                       CurrentUser.setName(context, user.getName());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: ", error.toException());
            }
        });
    }

    public static void isUserSavedCall(Context context, String id, ResponseInterface responseInterface) {
        SpotsDialog dialog = AppHelper.showLoadingDialog(context);
        dialog.show();
        rideRef.keepSynced(true);
        Query query = userRef.orderByChild("id").equalTo(id);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                responseInterface.onResponse(snapshot.exists());
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: isUserSaved ", error.toException());
                responseInterface.onError(error.getDetails());
                dialog.dismiss();

            }
        });
    }

    public static void removeFromRideProgressCall(RideProgress rideProgress,
                                                  ResponseInterface responseInterface) {
        progressRidesRef.child(rideProgress.getId()).removeValue().addOnCompleteListener(task -> {
            responseInterface.onResponse(task.isSuccessful());
        }).addOnFailureListener(e -> {
            Log.e(TAG, "onFailure: removeFromRideAcceptCall ", e);
            responseInterface.onError(e.getMessage());
        });

    }

    public static void setRideCompletedCall(Context context, RideProgress rideProgress, ResponseInterface responseInterface) {
        SpotsDialog dialog = AppHelper.showLoadingDialog(context);
        dialog.show();
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
                    completedRide.setDistance(rideProgress.getDistance());
                    completedRide.setDuration(rideProgress.getDuration());
                    completedRide.setPrice(rideProgress.getPrice());
                    completedRide.setDropOffTimeStamp(AppHelper.getTimeStamp());

                    completedRidesRef.child(rideProgress.getId()).setValue(completedRide)
                            .addOnCompleteListener(task -> {
                                dialog.dismiss();
                                responseInterface.onResponse(task.isSuccessful());
                            }).addOnFailureListener(e -> {
                        dialog.dismiss();
                        Log.e(TAG, "onFailure: setRideCompleteCall ", e);
                        responseInterface.onError(e.getMessage());
                    });
                } else {
                    dialog.dismiss();
                    Log.d(TAG, "onResponse: setRideCompleteCall Response from removeFromRideAcceptCall is false");
                }
            }

            @Override
            public void onError(String error) {
                dialog.dismiss();
                Log.e(TAG, "onError: setRideCompleteCall Error from removeFromRideAcceptCall " + error);
            }
        });
    }

    public static void isRideAcceptedCall(Context context, String id, ResponseInterface responseInterface) {
        SpotsDialog dialog = AppHelper.showLoadingDialog(context);
        dialog.show();
        Query query = progressRidesRef.orderByChild("id").equalTo(id);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    responseInterface.onResponse(true);
                    dialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
                Log.e(TAG, "onCancelled: " + "isRideAcceptedCall ", error.toException());
                responseInterface.onError(error.getMessage());
            }
        });
    }

    public static void setRideAcceptCall(Context context, Ride ride, ResponseInterface responseInterface) {
        SpotsDialog dialog = AppHelper.showLoadingDialog(context);
        dialog.show();
        removeFromRideCall(ride, new ResponseInterface() {
            @Override
            public void onResponse(Object... params) {
                if ((boolean) params[0]) {
                    RideProgress progress = new RideProgress();
                    progress.setDriverId(CurrentUser.getUserId());
                    progress.setDriverName(CurrentUser.getName(context));
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
                    progress.setPrice(ride.getPrice());
                    progress.setDistance(ride.getDistance());
                    progress.setDuration(ride.getDuration());
                    progress.setRideStarted(false);
                    progress.setPickupTimeStamp("");
                    progressRidesRef.child(ride.getId()).setValue(progress)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    dialog.dismiss();
                                    responseInterface.onResponse(true, progress);
                                } else {
                                    dialog.dismiss();
                                    responseInterface.onResponse(false);
                                }
                            }).addOnFailureListener(e -> {
                        dialog.dismiss();
                        Log.e(TAG, "onFailure: setRideAcceptCall ", e);
                        responseInterface.onError(e.getMessage());
                    });
                } else {
                    dialog.dismiss();
                    Log.d(TAG, "onResponse: setRideAcceptCall Response from removeRideCall is false");
                }
            }

            @Override
            public void onError(String error) {
                dialog.dismiss();
                Log.e(TAG, "onError: setRideAcceptCall Error from removeRideCall " + error);
            }
        });
    }

    public static void setRideProgressCall(Context context, String id, ResponseInterface responseInterface) {
        SpotsDialog dialog = AppHelper.showLoadingDialog(context);
        dialog.show();
        progressRidesRef.child(id).child("rideStarted").setValue(true)
                .addOnCompleteListener(task -> {
                    progressRidesRef.child(id).child("pickupTimeStamp").setValue(AppHelper.getTimeStamp())
                            .addOnCompleteListener(task1 -> {
                                responseInterface.onResponse(task1.isSuccessful());
                                dialog.dismiss();
                            }).addOnFailureListener(e -> {
                        dialog.dismiss();
                        Log.e(TAG, "onFailure: setRideAcceptCall Inner ", e);
                        responseInterface.onError(e.getMessage());
                    });
                }).addOnFailureListener(e -> {
            dialog.dismiss();
            Log.e(TAG, "onFailure: setRideAcceptCall ", e);
            responseInterface.onError(e.getMessage());
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

    public static void getMyCompletedRidesAsDiverCall(Context context, ResponseInterface responseInterface) {
        SpotsDialog dialog = AppHelper.showLoadingDialog(context);
        dialog.show();
        List<CompletedRide> completedRideList = new ArrayList<>();
        Query query = completedRidesRef.orderByChild("driverId").equalTo(CurrentUser.getUserId());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        if (dataSnapshot.exists()) {
                            CompletedRide completedRide = dataSnapshot.getValue(CompletedRide.class);
                            if (completedRide != null) {
                                completedRideList.add(completedRide);
                            }
                        }
                    }
                    responseInterface.onResponse(completedRideList);
                    dialog.dismiss();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
                Log.e(TAG, "onCancelled: getMyCompletedRideAsDiverCall ", error.toException());
                responseInterface.onError(error.getMessage());
            }
        });
    }

    public static void getMyCompletedRidesAsPassengerCall(Context context, ResponseInterface responseInterface) {
        SpotsDialog dialog = AppHelper.showLoadingDialog(context);
        dialog.show();
        List<CompletedRide> completedRideList = new ArrayList<>();
        Query query = completedRidesRef.orderByChild("passengerId").equalTo(CurrentUser.getUserId());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        if (dataSnapshot.exists()) {
                            CompletedRide completedRide = dataSnapshot.getValue(CompletedRide.class);
                            if (completedRide != null) {
                                completedRideList.add(completedRide);
                            }
                        }
                    }
                    responseInterface.onResponse(completedRideList);
                    dialog.dismiss();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
                Log.e(TAG, "onCancelled: getMyCompletedRideAsPassengerCall ", error.toException());
                responseInterface.onError(error.getMessage());
            }
        });
    }
}
