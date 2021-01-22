package com.example.projectuber.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class CurrentUser {

    private static final String CURRENT_USER_SESSION = "CurrentUser";
    private static final String DrivingMode = "DrivingMode";

    public static String getUserId() {
        if (AppHelper.isUserAvailable()) {
            return FirebaseAuth.getInstance().getUid();
        }
        return "null";
    }
    public static String getUsersPhoneNum() {
        if (AppHelper.isUserAvailable()) {
            return Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getPhoneNumber();
        }
        return "null";
    }
    public static void signOut() {
        if (AppHelper.isUserAvailable()) {
            FirebaseAuth.getInstance().signOut();
        }
    }

    public static void setDrivingMode(Context context, boolean isDriver) {
        SharedPreferences.Editor sharedPref = context.getSharedPreferences(CURRENT_USER_SESSION,
                Context.MODE_PRIVATE).edit();
        sharedPref.putBoolean(DrivingMode, isDriver);
        sharedPref.apply();
    }

    public static boolean IsDriver(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(CURRENT_USER_SESSION,
                Context.MODE_PRIVATE);
        return sharedPref.getBoolean(DrivingMode, false);
    }

}
