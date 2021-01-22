package com.example.projectuber;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.projectuber.Interfaces.ResponseInterface;
import com.example.projectuber.Models.Ride;
import com.example.projectuber.Models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DatabaseCalls {

    private static final String TAG = "DatabaseCalls";

    private static final String Users = "Users";
    private static final String Rides = "Rides";
    private static final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference(Users);
    private static final DatabaseReference rideRef = FirebaseDatabase.getInstance().getReference(Rides);


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
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
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
}
