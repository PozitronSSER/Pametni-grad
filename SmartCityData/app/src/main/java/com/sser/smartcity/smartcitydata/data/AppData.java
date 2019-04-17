package com.sser.smartcity.smartcitydata.data;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;

import com.sser.smartcity.smartcitydata.ActivityDataUpdater;
import com.sser.smartcity.smartcitydata.networking.JsonHandler;
import com.sser.smartcity.smartcitydata.networking.UpdateDataHandler;

import java.util.ArrayList;

// Stores global application data
public class AppData {

    // Current activity for accessing it from static methods, must be accessed in try-catch block in case that activity is closed
    @SuppressLint("StaticFieldLeak")
    public static Activity currentActivity;


    // Unique index for each category for knowing witch has been opened
    public static final int noCategoryTypeDefaultIndex = 0;
    public static final int meteorologicalStationCategoryTypeIndex = 1;
    public static final int streetLightCategoryTypeIndex = 2;
    public static final int cameraCategoryTypeIndex = 3;
    public static final int parkingCategoryTypeIndex = 4;
    public static final int parkingTicketCategoryTypeIndex = 5;

    // Storing unique category index for current category
    public static int lastClickedCategoryTypeIndex = noCategoryTypeDefaultIndex;


    // Final number of thingspeak channels to get data from
    public static final int numberOfThingSpeakChannels = 3;

    // Each thingspeak channel urls
    private static final String JSON_REQUEST_MET_STATION = "https://api.thingspeak.com/channels/196696/feeds.json?api_key=E5V4C2KMUN5S4BWM";
    private static final String JSON_REQUEST_PARKING = "https://api.thingspeak.com/channels/629316/feeds.json?api_key=YKPQUEH22XA0MPVE";
    private static final String JSON_REQUEST_CAMERA = "https://api.thingspeak.com/channels/736965/feeds.json?api_key=XGPRBTB6ZGWVKOQM";


    // Each thingspeak channel final urls for getting data from them
    public static final String metStationRequestUrl, parkingRequestUrl, cameraRequestUrl;

    // The static is called first time when class is mentioned (before everything else)
    static {
        // add query parameters in string requests
        metStationRequestUrl = JsonHandler.makeURL(JSON_REQUEST_MET_STATION, true);
        parkingRequestUrl = JsonHandler.makeURL(JSON_REQUEST_PARKING, true);
        cameraRequestUrl = JsonHandler.makeURL(JSON_REQUEST_CAMERA, false);
    }


    // Storing data of all meteorological stations
    public static ArrayList<MeteorologicalStation> meteorologicalStations = new ArrayList<>();
    // Storing data of all cameras
    public static ArrayList<Camera> cameras = new ArrayList<>();
    // Storing data of all parkings
    public static ArrayList<Parking> parkings = new ArrayList<>();


    // Does data need to refresh (and show loading progress bar)
    public static boolean dataNeedsRefresh = true;

    // Index of last clicked station in CategoryListActivity
    public static int lastClickedStationListIndex = -1;


    // Read string from resources
    public static String readResource(int id, Context context) {
        if(context == null) {
            return "";
        }
        return context.getResources().getString(id);
    }

    // Handle resuming activity (activities have lot of same operations)
    public static void resumeActivity(Activity activity, int stationListIndex) {
        // Store current activity for accessing it later
        currentActivity = activity;


        // Setup layout for showing "no internet connection" warning
        ActivityDataUpdater.setNetworkState(activity);
        // Setup layout for showing loading progress bar
        ActivityDataUpdater.setLoadingProgressBar(activity);

        // Refresh activity layout (loading progress bar, no internet connection warning...)
        try {
            ActivityDataUpdater.updateActivityData(activity, stationListIndex);
        } catch (Exception ignored) {}

        // Start getting data from the internet (passing this activity as current one)
        UpdateDataHandler.startUpdatingData(activity);
    }
}
