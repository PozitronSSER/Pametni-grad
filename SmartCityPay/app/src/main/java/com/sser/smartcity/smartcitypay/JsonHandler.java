package com.sser.smartcity.smartcitypay;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

// Handles JSON actions
class JsonHandler {

    private static final String LOG_TAG = JsonHandler.class.getName();

    // Number of while(true) loops running and waiting for thingspeak to pass 15 sec limit
    static int waitLoopCount = 0;

    // Add query parameters in string request (for adding and removing user plate)
    private static String makeNewPlateDataUrl(String request, String userId, String plate, boolean addPlate) {

        Uri baseUri = Uri.parse(request);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("field1", userId);
        uriBuilder.appendQueryParameter("field2", plate);
        uriBuilder.appendQueryParameter("field3", addPlate ? "1" : "0");

        return uriBuilder.toString();
    }

    // Add query parameters in string request (for adding new user balance (and overriding old one if it exists))
    private static String makeNewUserBalanceUrl(String request, String userId, float balance) {

        Uri baseUri = Uri.parse(request);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("field1", userId);
        uriBuilder.appendQueryParameter("field2", String.valueOf(balance));

        return uriBuilder.toString();
    }

    // Make an HTTP request to the given URL and return a String as the response.
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        if(url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if(urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            }
            else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "Problem retrieving a JSON results.", e);
        } finally {
            if(urlConnection != null) {
                urlConnection.disconnect();
            }
            if(inputStream != null) {
                inputStream.close();
            }
        }

        return jsonResponse;
    }


    // Convert the InputStream into a String which contains the whole JSON response from the server.
    private static String readFromStream(InputStream inputStream) throws Exception{
        StringBuilder output = new StringBuilder();
        if(inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while(line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }

        return output.toString();
    }


    // Saves data from JSON (loop through all thingspeak data and save all plates for current user)
    static void getAllUserPlates() throws Exception {

        String jsonResponse = null;

        // Create URL object
        URL url = null;

        try {
            url = new URL("https://api.thingspeak.com/channels/749743/feeds.json?api_key=P1WAZNYZ5NHSGENY");
        }
        catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }

        // Perform HTTP request to the URL and receive a JSON response back
        try {
            jsonResponse = JsonHandler.makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(jsonResponse)) {
            return;
        }

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            JSONObject jsonObject = new JSONObject(jsonResponse);

            JSONArray feeds = jsonObject.getJSONArray("feeds");

            String userId = "";
            String userPlate = "";

            // Loop through all data and save all current user's data (if valid)
            for(int i = 0; i < feeds.length(); i++) {
                JSONObject obj2 = feeds.getJSONObject(i);

                userId = obj2.getString("field1");

                // If user Id is saved as the current user's Id handle adding data change
                if(userId != null && userId.equals(AppData.firebaseUser.getUid())) {
                    userPlate = obj2.getString("field2");

                    // If user plate if valid, add/remove it
                    if(userPlate != null && !userPlate.isEmpty()) {

                        // Should this plate be added to the list
                        boolean addPlate = false;

                        if(obj2.getString("field3").equals("1")) {
                            // If plate should be added, that is default value
                            addPlate = true;
                        }

                        // Loop through all existing plates
                        for(int j = 0; j < AppData.userPlates.size(); j++) {
                            if(AppData.userPlates.get(j).getPlate().equals(userPlate)) {
                                // New plate already exist in the list

                                if(obj2.getString("field3").equals("1")) {
                                    // Plate shouldn't be added (as it is already saved)
                                    addPlate = false;
                                    break;
                                } else {
                                    // Remove all same plates as the current one (that have been saved so far)
                                    AppData.userPlates.remove(j--);
                                }
                            }
                        }

                        // Add plate to the list if it should be added
                        if(addPlate) {
                            AppData.userPlates.add(new Plate(userPlate));
                        }

                    }
                }

            }

        } catch (Exception e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e(LOG_TAG, "Problem parsing JSON results", e);
        }

    }


    // Writes new plate to the thingspeak channel
    static void setNewPlateData(String plateText, boolean addPlate) throws Exception {

        // Create URL object
        URL url = null;

        try {
            url = new URL(makeNewPlateDataUrl("https://api.thingspeak.com/update?api_key=JY4A7T52CUAJ0BDW",
                    AppData.firebaseUser.getUid(), plateText, addPlate));
        }
        catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }

        // Send data to the channel
        startSendingDataUntilSuccess(url);

    }



    // Saves data from JSON (loop through all thingspeak data and get current user's balance)
    static void getUserBalance() throws Exception {

        String jsonResponse = null;

        // Create URL object
        URL url = null;

        try {
            url = new URL("https://api.thingspeak.com/channels/751934/feeds.json?api_key=W0XQM80G1VYHRZ3T");
        }
        catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }

        // Perform HTTP request to the URL and receive a JSON response back
        try {
            jsonResponse = JsonHandler.makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(jsonResponse)) {
            return;
        }

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            JSONObject jsonObject = new JSONObject(jsonResponse);

            JSONArray feeds = jsonObject.getJSONArray("feeds");

            String userId = "";
            Float userBalance = null;

            // Loop through all data and save most recent one (that belongs to current user)
            for(int i = 0; i < feeds.length(); i++) {
                JSONObject obj2 = feeds.getJSONObject(i);

                userId = obj2.getString("field1");

                // If user Id is saved as the current user's Id update balance
                if(userId != null && userId.equals(AppData.firebaseUser.getUid())) {
                    try {
                        userBalance = Float.parseFloat(obj2.getString("field2"));
                    } catch (Exception ignored) {}
                }

            }

            // For checking if userId is valid or user is logged out
            int userIdStringLenght = 0;
            try {
                userIdStringLenght = AppData.firebaseAuth.getCurrentUser().getUid().length();
            } catch (NullPointerException ignored) {}

            // Set this to the default (in case userId is not valid)
            AppData.userBalance = 0;

            // If user id is valid / user is logged in - save balance
            if(userIdStringLenght > 5) {
                if(userBalance == null) {
                    // If there is no balance data from current user, set balance to current balance (always 0)
                    setUserBalance(AppData.userBalance);
                } else {
                    // If current user's balance is found, save it
                    AppData.userBalance = userBalance;
                }
            }

        } catch (Exception e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e(LOG_TAG, "Problem parsing JSON results", e);
        }

    }


    // Writes new user balance to the thingspeak channel
    static void setUserBalance(float balance) throws Exception {

        // Create URL object
        URL url = null;

        try {
            url = new URL(makeNewUserBalanceUrl("https://api.thingspeak.com/update?api_key=7USWNBPGRUNM2HIT",
                    AppData.firebaseUser.getUid(), balance));
        }
        catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }

        // Send data to the channel
        startSendingDataUntilSuccess(url);


    }

    // Loops and tries to send data until it is successfully sent
    private static void startSendingDataUntilSuccess(URL url) {

        // It will start to loop, so increase counter
        waitLoopCount++;

        // Initial response is error response (if we didn't got the response- loop until we get it)
        String jsonResponse = "-1";

        // Don't wait and show show loading animation first time
        boolean firstTime = true;

        while(Integer.parseInt(jsonResponse) <= 0) {

            // Perform HTTP request to the URL and receive a JSON response back
            try {
                jsonResponse = JsonHandler.makeHttpRequest(url);

                // If there is an error, sleep between loop steps (but not first time - in case everything is OK)
                if(!firstTime) {
                    // Try to show a loading animation (it is already showing - in case it closes)
                    ((HomeActivity) AppData.currentActivity).changeRefreshAnimationState(true);
                    Thread.sleep(100);
                }
                firstTime = false;
            } catch (Exception e) {
                Log.e(LOG_TAG, "Problem making the HTTP request.", e);
            }
        }

        // Everything is done, decrease counter back
        waitLoopCount--;
    }





}
