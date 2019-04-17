package com.sser.smartcity.smartcitydata.networking;

import android.app.Activity;
import android.util.Log;

import com.sser.smartcity.smartcitydata.ActivityDataUpdater;
import com.sser.smartcity.smartcitydata.data.AppData;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

// Interface got getting data from the internet
public class UpdateDataHandler {

    private static final String LOG_TAG = UpdateDataHandler.class.getName();


    // Thread on witch data is periodically refreshing
    private static Thread updateThread = null;

    // Just get new data from the internet (and update UI afterwards)
    public static void updateData(final Activity activity) {
        for(int channel = 1; channel <= AppData.numberOfThingSpeakChannels; channel++) {
            // Create URL object
            URL url = null;

            try {
                switch (channel) {
                    case 1:
                        url = new URL(AppData.metStationRequestUrl);
                        break;
                    case 2:
                        url = new URL(AppData.parkingRequestUrl);
                        break;
                    case 3:
                        url = new URL(AppData.cameraRequestUrl);
                        break;
                    default:
                        Log.e(LOG_TAG, "Channel could not be handled");
                }
            }
            catch (MalformedURLException e) {
                Log.e(LOG_TAG, "Problem building the URL ", e);
            }

            // Perform HTTP request to the URL and receive a JSON response back
            String jsonResponse = null;

            try {
                jsonResponse = JsonHandler.makeHttpRequest(url);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Problem making the HTTP request.", e);
            }

            // "convert" JSON to data and save it
            JsonHandler.extractFeatureFromJson(jsonResponse, channel);
        }

        // Data doesn't need to refresh anymore
        AppData.dataNeedsRefresh = false;

        // Try to update UI
        try {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        ActivityDataUpdater.updateActivityData(activity, AppData.lastClickedStationListIndex);
                    } catch (Exception ignored) {}
                }
            });
        } catch (Exception ignored) {}

    }

    // Start periodically checking for new data (running in the background)
    public static void startUpdatingData(final Activity activity) {
        // If thread is already running, stop id
        try {
            updateThread.interrupt();
            updateThread = null;
        } catch (Exception ignored) {}

        // Start checking and assign this thread to the updateThread
        updateThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while(true) {
                        updateData(activity);

                        try {
                            Thread.sleep(15000);
                        } catch (Exception e) {
                            // Exception is thrown when thread is interrupted, in that case, exit infinity loop
                            return;
                        }
                    }
                } catch (Exception ignored) {}
            }
        });
        updateThread.start();
    }



}
