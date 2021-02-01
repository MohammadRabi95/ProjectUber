package com.vcrow.rides;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.vcrow.rides.Interfaces.ResponseInterface;
import com.vcrow.rides.Models.Car;
import com.vcrow.rides.Models.CompletedRide;
import com.vcrow.rides.Models.Ride;
import com.vcrow.rides.Models.RideProgress;
import com.vcrow.rides.Models.User;
import com.vcrow.rides.Utils.AppHelper;
import com.vcrow.rides.Utils.CurrentUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class DatabaseCalls {

    private static final String TAG = "DatabaseCalls";

    private static final String Users = "Users";
    private static final String Rides = "Rides";
    public static final DatabaseReference rideRef = FirebaseDatabase.getInstance().getReference(Rides);
    private static final String ProgressRides = "ProgressRides";
    private static final String CompletedRides = "CompletedRides";
    private static final String Cars = "Cars";
    private static final String CarsImages = "CarImages";
    private static final String LicenseImages = "LicenseImages";
    private static final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference(Users);
    private static final DatabaseReference progressRidesRef = FirebaseDatabase.getInstance().getReference(ProgressRides);
    private static final DatabaseReference carsRef = FirebaseDatabase.getInstance().getReference(Cars);
    private static final DatabaseReference completedRidesRef = FirebaseDatabase.getInstance().getReference(CompletedRides);
    private static final StorageReference carImagesRef = FirebaseStorage.getInstance().getReference(CarsImages);
    private static final StorageReference licenseImagesRef = FirebaseStorage.getInstance().getReference(LicenseImages);

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

    private static void removeFromRideProgressCall(RideProgress rideProgress, ResponseInterface responseInterface) {
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
                    completedRide.setAmountPaid(false);
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

    public static void isRideStartedCall(Context context, String id, ResponseInterface responseInterface) {
        SpotsDialog dialog = AppHelper.showLoadingDialog(context);
        dialog.show();
        progressRidesRef.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    RideProgress progress = snapshot.getValue(RideProgress.class);
                    if (progress != null) {
                        responseInterface.onResponse(progress);
                        dialog.dismiss();
                    }
                } else {
                    dialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
                Log.e(TAG, "onCancelled: " + "isRideStartedCall ", error.toException());
                responseInterface.onError(error.getMessage());
            }
        });
    }

    public static void isRideCompletedCall(Context context, String id, ResponseInterface responseInterface) {
        SpotsDialog dialog = AppHelper.showLoadingDialog(context);
        dialog.show();
        completedRidesRef.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    CompletedRide completedRide = snapshot.getValue(CompletedRide.class);
                    responseInterface.onResponse(true, completedRide);
                } else {
                    responseInterface.onResponse(false);
                }
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
                Log.e(TAG, "onCancelled: " + "isRideStartedCall ", error.toException());
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
            responseInterface.onResponse(task.isSuccessful());
        }).addOnFailureListener(e -> {
            Log.e(TAG, "onFailure: removeRideCall ", e);
            responseInterface.onError(e.getMessage());
        });
    }

    public static void getMyCompletedRidesCall(Context context, String param, ResponseInterface responseInterface) {
        SpotsDialog dialog = AppHelper.showLoadingDialog(context);
        dialog.show();
        List<CompletedRide> completedRideList = new ArrayList<>();
        Query query = completedRidesRef.orderByChild(param).equalTo(CurrentUser.getUserId());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        if (dataSnapshot.exists()) {
                            CompletedRide completedRide = dataSnapshot.getValue(CompletedRide.class);
                            if (completedRide != null) {
                                completedRideList.add(completedRide);
                            }
                        }
                    }
                    responseInterface.onResponse(completedRideList);
                }
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
                Log.e(TAG, "onCancelled: getMyCompletedRideCall ", error.toException());
                responseInterface.onError(error.getMessage());
            }
        });
    }

    public static void isUserDriver(Context context, ResponseInterface responseInterface) {
        SpotsDialog spotsDialog = AppHelper.showLoadingDialog(context);
        spotsDialog.show();
        userRef.child(CurrentUser.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        responseInterface.onResponse(user.isDriver());
                        spotsDialog.dismiss();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: isUserDriver ", error.toException());
                responseInterface.onError(error.getMessage());
                spotsDialog.dismiss();
            }
        });
    }

    public static void isPaying(Context context, String id, ResponseInterface responseInterface) {

        completedRidesRef.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    CompletedRide completedRide = snapshot.getValue(CompletedRide.class);
                    if (completedRide != null) {
                        if (!"".equals(completedRide.getPaidVia())) {
                            responseInterface.onResponse(completedRide.getPaidVia());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: isUserDriver ", error.toException());
                responseInterface.onError(error.getMessage());
            }
        });
    }

    public static void setPaymentMethod(Context context, String id, String method, ResponseInterface responseInterface) {

        completedRidesRef.child(id).child("paidVia").setValue(method)
                .addOnCompleteListener(task -> responseInterface.onResponse(task.isSuccessful())).addOnFailureListener(e -> {
            Log.e(TAG, "onFailure: setPaymentMethod ", e);
            responseInterface.onError(e.getMessage());
        });
    }

    public static void setPaymentPaid(Context context, String id, boolean isPaid, ResponseInterface responseInterface) {

        completedRidesRef.child(id).child("amountPaid").setValue(isPaid)
                .addOnCompleteListener(task -> responseInterface.onResponse(task.isSuccessful())).addOnFailureListener(e -> {
            Log.e(TAG, "onFailure: setPaymentMethod ", e);
            responseInterface.onError(e.getMessage());
        });
    }

    public static void registerDriverCall(Context context, Car car, Uri licenseIMG,
                                          Uri carIMG, ResponseInterface responseInterface) {
        SpotsDialog dialog = AppHelper.showLoadingDialog(context);
        dialog.show();
        uploadLicenseImageCall(context, licenseIMG, new ResponseInterface() {
            @Override
            public void onResponse(Object... params) {
                if ((boolean) params[0]) {
                    String url = (String) params[1];
                    car.setLicenseImageURl(url);
                    uploadCarsImageCall(context, carIMG, new ResponseInterface() {
                        @Override
                        public void onResponse(Object... params) {
                            if ((boolean) params[0]) {
                                String url = (String) params[1];
                                car.setCarImageURL(url);
                                carsRef.child(CurrentUser.getUserId())
                                        .setValue(car).addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        userRef.child(CurrentUser.getUserId())
                                                .child("driver").setValue(true)
                                                .addOnCompleteListener(task1 -> {
                                                    dialog.dismiss();
                                                    responseInterface.onResponse(task1.isSuccessful());
                                                });
                                    }
                                });
                            }
                        }

                        @Override
                        public void onError(String error) {
                            dialog.dismiss();
                            responseInterface.onError(error);
                        }
                    });
                }
            }

            @Override
            public void onError(String error) {
                dialog.dismiss();
                responseInterface.onError(error);
            }
        });
    }

    private static void uploadCarsImageCall(Context context, Uri carIMG,
                                            ResponseInterface responseInterface) {
        StorageReference ref = carImagesRef.child(System.currentTimeMillis() + "" + "." +
                AppHelper.getFileExtension(context, carIMG));
        ref.putFile(carIMG).addOnSuccessListener(taskSnapshot ->
                ref.getDownloadUrl().addOnSuccessListener(uri ->
                        responseInterface.onResponse(true, uri.toString()))
                        .addOnFailureListener(e -> {
                            responseInterface.onError(e.getMessage());
                            Log.e(TAG, "onFailure: uploadCarsImageCall 1 ", e);
                        })).addOnFailureListener(e -> {
            responseInterface.onError(e.getMessage());
            Log.e(TAG, "onFailure: uploadCarsImageCall 2 ", e);
        });
    }

    private static void uploadLicenseImageCall(Context context, Uri licenseIMG,
                                               ResponseInterface responseInterface) {
        StorageReference ref = licenseImagesRef.child(System.currentTimeMillis() + "" + "." +
                AppHelper.getFileExtension(context, licenseIMG));
        ref.putFile(licenseIMG).addOnSuccessListener(taskSnapshot ->
                ref.getDownloadUrl().addOnSuccessListener(uri ->
                        responseInterface.onResponse(true, uri.toString()))
                        .addOnFailureListener(e -> {
                            responseInterface.onError(e.getMessage());
                            Log.e(TAG, "onFailure: uploadLicenseImageCall 1 ", e);
                        })).addOnFailureListener(e -> {
            responseInterface.onError(e.getMessage());
            Log.e(TAG, "onFailure: uploadLicenseImageCall 2 ", e);
        });
    }

    public static void getCarInfo(Context context, String id, ResponseInterface responseInterface) {
        carsRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Car car = snapshot.getValue(Car.class);
                    if (car != null) {
                        responseInterface.onResponse(true, car);
                    } else {
                        responseInterface.onResponse(false);
                    }
                } else {
                    responseInterface.onResponse(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: ", error.toException());
                responseInterface.onError(error.getMessage());
            }
        });
    }

    public static void isAmountPaid(Context context, String id, ResponseInterface responseInterface) {
        completedRidesRef.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    CompletedRide completedRide = snapshot.getValue(CompletedRide.class);
                    responseInterface.onResponse(completedRide.isAmountPaid());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: ", error.toException());
                responseInterface.onError(error.getMessage());
            }
        });
    }

}
