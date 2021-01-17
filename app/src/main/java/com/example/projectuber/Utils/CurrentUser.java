package com.example.projectuber.Utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class CurrentUser {

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
}
