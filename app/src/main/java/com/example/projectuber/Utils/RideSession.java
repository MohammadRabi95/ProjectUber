package com.example.projectuber.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.projectuber.Models.Ride;
import com.example.projectuber.Models.RideProgress;
import com.google.gson.Gson;

public class RideSession {

    private static final String RIDE_SESSION = "Ride_Session";
    private static final String rideInProgress = "in_progress";
    private static final String rideAccepted = "ride_accepted";
    private static final String rideModel = "ride_model";

    public static void setRideInProgress(Context context, boolean isRideStarted) {
        SharedPreferences.Editor sharedPref = context.getSharedPreferences(RIDE_SESSION,
                Context.MODE_PRIVATE).edit();
        sharedPref.putBoolean(rideInProgress, isRideStarted);
        sharedPref.apply();

    }

    public static boolean IsRideInProgress(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(RIDE_SESSION,
                Context.MODE_PRIVATE);
        return sharedPref.getBoolean(rideInProgress, false);
    }

    public static void setRideAccepted(Context context, boolean isRideAccepted) {
        SharedPreferences.Editor sharedPref = context.getSharedPreferences(RIDE_SESSION,
                Context.MODE_PRIVATE).edit();
        sharedPref.putBoolean(rideAccepted, isRideAccepted);
        sharedPref.apply();

    }

    public static boolean IsRideAccepted(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(RIDE_SESSION,
                Context.MODE_PRIVATE);
        return sharedPref.getBoolean(rideAccepted, false);
    }

    public static void setRideModel(Context context, RideProgress ride) {
        SharedPreferences.Editor sharedPref = context.getSharedPreferences(RIDE_SESSION,
                Context.MODE_PRIVATE).edit();
        Gson gson = new Gson();
        String json = gson.toJson(ride);
        sharedPref.putString(rideModel, json);
        sharedPref.apply();

    }

    public static RideProgress getRideModel(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(RIDE_SESSION,
                Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPref.getString(rideModel, "");
        return gson.fromJson(json, RideProgress.class);
    }
}
