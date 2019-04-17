package com.sser.smartcity.smartcitypay;


import android.annotation.SuppressLint;
import android.app.Activity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

// Stores all global application data
class AppData {

    // Keeps current activity for accessing it from static methods
    @SuppressLint("StaticFieldLeak")
    static Activity currentActivity = null;

    // Firebase auth and user for accessing user's data
    static FirebaseAuth firebaseAuth;
    static FirebaseUser firebaseUser;

    // List of all user's plates
    static ArrayList<Plate> userPlates = new ArrayList<>();

    // User's current account balance (money) in kuna
    static float userBalance = 0;


}
