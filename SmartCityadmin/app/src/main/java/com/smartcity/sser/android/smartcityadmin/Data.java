package com.smartcity.sser.android.smartcityadmin;

import android.util.Log;
import android.view.View;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

public class Data { // For saving static data in application

    private static final String LOG_TAG = StaniceActivity.class.getName();

    public static String requestUrl1, requestUrl2;

    /*
    // URLs for JSON
    public static final String JSON_REQUEST_URL1 = "https://api.thingspeak.com/channels/630034/feeds.json?api_key=Y4KQ33L0LZM0GSZX";
    public static final String JSON_REQUEST_URL2 = "https://api.thingspeak.com/channels/630036/feeds.json?api_key=F62D045TYTBVDYQY";
    public static final String JSON_REQUEST_URL3 = "https://api.thingspeak.com/channels/630038/feeds.json?api_key=5VIJHN6T69PQ30L8";
    */

    // URLs for JSON
    public static final String JSON_REQUEST_MET_STANICA = "https://api.thingspeak.com/channels/196696/feeds.json?api_key=E5V4C2KMUN5S4BWM";
    public static final String JSON_REQUEST_PARKING = "https://api.thingspeak.com/channels/629316/feeds.json?api_key=YKPQUEH22XA0MPVE";


    public static final int numberOfChannels = 2;

    // Saving data from all Stanicas
    public static Vector<Stanica> sveStanice = new Vector<>();

    // Does mainActivity need to refresh (update data, get new JSON response)
    // True when opening app first time
    public static boolean refresh = true;

    public static int lastClickedOnStanicaPosition = 0;


    public static void doRefreshes() {
        // add query parameters in string requests
        requestUrl1 = JSONHandler.makeURL(Data.JSON_REQUEST_MET_STANICA);
        requestUrl2 = JSONHandler.makeURL(Data.JSON_REQUEST_PARKING);

        Thread mainRefreshThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while(true) {
                        refresh();

                        try {
                            Thread.sleep(5000);
                        } catch (Exception e) {}
                    }
                } catch (Exception e) {

                }
            }
        });
        mainRefreshThread.start();
    }

    // Get JSON responses from all channels
    public static void refresh() {

        Thread updateThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for(int channel = 1; channel <= Data.numberOfChannels; channel++) {
                        // Create URL object
                        URL url = null;

                        try {
                            switch (channel) {
                                case 1:
                                    url = new URL(requestUrl1);
                                    break;
                                case 2:
                                    url = new URL(requestUrl2);
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
                            jsonResponse = JSONHandler.makeHttpRequest(url);
                        } catch (IOException e) {
                            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
                        }

                        // "convert" JSON to data and save it
                        JSONHandler.extractFeatureFromJson(jsonResponse, channel);
                    }

                    Data.refresh = false;
                } catch (Exception e) {

                }


            }
        });
        updateThread.start();
    }


}
