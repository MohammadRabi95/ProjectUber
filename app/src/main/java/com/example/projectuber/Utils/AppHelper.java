package com.example.projectuber.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AppHelper {

    public static void showSnackBar(View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    public static boolean isInternetAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;
    }

    public static boolean isUserAvailable() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    public static String calculateFairs(float distance, float duration) {
        float fairPer1Km = 0.25f; // dummy fairs
        float fairPer1Min = 0.05f;
        float total = (fairPer1Km * distance) + (fairPer1Min * duration);
        return String.valueOf(total);
    }

    public static String getTimeStamp() {
        return "";
    }

}
