package com.sser.smartcity.smartcitydata;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

public class GoogleMapsHandler {

    /*
     * Constant latitude and longitude for each category (for only stations in each category)
     * TODO: make this more efficient (flexible)
     */

    // Parking coordinates
    public static final String parkingLat = "45.805110";
    public static final String parkingLong = "15.952376";

    // Camera coordinates
    public static final String cameraLat = "45.779008";
    public static final String cameraLong = "15.971656";

    // Meteorological station coordinates
    public static final String meteorologicalLat = "45.335779";
    public static final String meteorologicalLong = "14.419240";


    // Open google maps activity with given latitude, longitude and label (name) of station
    public static void openGoogleMaps(final Activity activity, String latitude, String longitude, String label) {
        try {
            // Create an Intent to open maps at given coordinates and with given label
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:<" + latitude + ">,<" + longitude +
                    ">?q=<" + latitude + ">,<" + longitude + ">(" + label.replace(" ", "+") +")"));
            // Make the Intent explicit by setting the Google Maps package
            mapIntent.setPackage("com.google.android.apps.maps");

            // Attempt to start an activity that can handle the Intent
            activity.startActivity(mapIntent);
        } catch (Exception e) {
            // If there is error opening activity, try to show error toast
            try {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Toast.makeText(activity, "Mapa se ne može otvoriti.", Toast.LENGTH_SHORT).show();
                        } catch (Exception ignored) {}
                    }
                });
            } catch (Exception ignored) {}
        }

    }



}
