package com.sser.smartcity.smartcitypay;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;


// Handles firebase actions
class FirebaseHandler {

    // Unique code for user login, for knowing what is returned on handling activity result
    private static final int REQUEST_CODE_LOGIN = 101;


    // Open activity for result (AuthUI) for email sign in
    static void authenticateUser(Activity activity) {
        activity.startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder()
                        .setAvailableProviders(getProviderList())
                        .build(),
                REQUEST_CODE_LOGIN);
    }

    // Handle user login (get all user data)
    static void loginUser() {
        // Set balance to be 0 (initially)
        AppData.userBalance = 0;
        // Delete all previous plates
        AppData.userPlates.clear();
        HomeActivity.updatePlatesList();


        try {
            // Try to show loading animation
            ((HomeActivity) AppData.currentActivity).changeRefreshAnimationState(true);
        } catch (Exception ignored) {}

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Get all data
                    JsonHandler.getAllUserPlates();
                    JsonHandler.getUserBalance();

                    // After we got new data, refresh layout
                    AppData.currentActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                HomeActivity.updatePlatesList();
                                ((HomeActivity) AppData.currentActivity).updateBalanceLayout();
                                // Close loading animation
                                ((HomeActivity) AppData.currentActivity).changeRefreshAnimationState(false);
                            } catch (Exception ignored) {}

                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    // Handle user logout
    static void logoutUser() {
        AppData.userBalance = 0;
        try {
            AppData.firebaseAuth.signOut();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // Handle return of activity for result (e.g. login authUI activity)
    static void handleActivityResult(Activity activity, int requestCode, int resultCode, @Nullable Intent data) {
        IdpResponse response = IdpResponse.fromResultIntent(data);

        if (requestCode == REQUEST_CODE_LOGIN) {
            // Activity with result was firebase sign in activity

            // User has just tried to log in
            if (resultCode == Activity.RESULT_OK) {
                // User logged in successfully
                Toast.makeText(activity, "Prijava uspješna", Toast.LENGTH_SHORT).show();

                // Save user (for getting some of it's data later on)
                AppData.firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


                // Handle user login
                loginUser();

            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }

    // Returns list of all firebase login types (only email)
    private static List<AuthUI.IdpConfig> getProviderList() {

        List<AuthUI.IdpConfig> providers = new ArrayList<>();

        providers.add(
                new AuthUI.IdpConfig.EmailBuilder().build());

        return providers;
    }



}
