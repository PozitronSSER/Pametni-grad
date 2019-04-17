package com.sser.smartcity.smartcitypay;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;


// First, login activity
public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Disable night mode (to fix some weird UI shit)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        // Get firebase authentication if user is already logged in
        AppData.firebaseAuth = FirebaseAuth.getInstance();


        // Setup login button
        Button loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseHandler.authenticateUser(MainActivity.this);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        // Save this activity as the current one for accessing it later (from static context)
        AppData.currentActivity = this;


        // Checks for internet connection and sets warning TextView accordingly
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        final boolean isConnected = activeNetwork != null && activeNetwork.isConnected();

        // Set error layout visibility
        View noInternetConnectionView = findViewById(R.id.no_internet_connection_TV);
        noInternetConnectionView.setVisibility(isConnected ? View.GONE : View.VISIBLE);



        // Try to login user
        if(AppData.firebaseAuth.getCurrentUser() != null) {
            // User is already logged in - get all data

            // Save user (for getting some of it's data later on)
            AppData.firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

            FirebaseHandler.loginUser();
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        } else {
            // User is not logged in - open login activity
            FirebaseHandler.authenticateUser(this);
            // When activity gives result it will call this onResume again and run the code above (HomeActivity will be opened)
        }

    }


    // Calls when activity for result (e.g. login authUI activity) returns result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Handle it
        FirebaseHandler.handleActivityResult(this, requestCode, resultCode, data);
    }

}
