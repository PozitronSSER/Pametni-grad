package com.sser.smartcity.smartcitydata.networking;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

// For handling network things
public class NetworkManager {

    private static final String LOG_TAG = NetworkManager.class.getName();

    // Returns true if there is connection to internet or false otherwise
    public static boolean checkNetworkState(Context context) {
        if(context == null) {
            return false;
        }

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        try {
            // Get details on the currently active default data network
            networkInfo = connMgr.getActiveNetworkInfo();
        } catch (NullPointerException e) {
            Log.e(LOG_TAG, "Cannot find active network connection");
        }

        // If there is a network connection, fetch data
        return (networkInfo != null && networkInfo.isConnected());

    }

}
