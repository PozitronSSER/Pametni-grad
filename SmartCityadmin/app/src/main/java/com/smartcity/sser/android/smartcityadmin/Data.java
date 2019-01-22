package com.smartcity.sser.android.smartcityadmin;

import java.util.Vector;

public class Data { // For saving static data in application

    // URLs for JSON
    public static final String JSON_REQUEST_URL1 = "https://api.thingspeak.com/channels/630034/feeds.json?api_key=Y4KQ33L0LZM0GSZX";
    public static final String JSON_REQUEST_URL2 = "https://api.thingspeak.com/channels/630036/feeds.json?api_key=F62D045TYTBVDYQY";
    public static final String JSON_REQUEST_URL3 = "https://api.thingspeak.com/channels/630038/feeds.json?api_key=5VIJHN6T69PQ30L8";

    public static final int numberOfChannels = 3;

    // Saving data from all Stanicas
    public static Vector<Stanica> sveStanice = new Vector<>();

    // Does mainActivity need to refresh (update data, get new JSON response)
    // True when opening app first time
    public static boolean refresh = true;

    public static int lastClickedOnStanicaPosition = 0;



}
