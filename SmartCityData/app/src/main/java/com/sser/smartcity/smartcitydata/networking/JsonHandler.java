package com.sser.smartcity.smartcitydata.networking;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.sser.smartcity.smartcitydata.data.AppData;
import com.sser.smartcity.smartcitydata.data.Camera;
import com.sser.smartcity.smartcitydata.data.MeteorologicalStation;
import com.sser.smartcity.smartcitydata.data.Parking;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;


// For handling JSON things
public class JsonHandler {

    private static final String LOG_TAG = JsonHandler.class.getName();

    // add query parameters in string request
    public static String makeURL(String request, boolean getOnlyLastData) {

        Uri baseUri = Uri.parse(request);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        if(getOnlyLastData) {
            uriBuilder.appendQueryParameter("results", "1");
        }

        return uriBuilder.toString();
    }

    // Make an HTTP request to the given URL and return a String as the response.
    static String makeHttpRequest(URL url) throws IOException {
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


    // Saves data from JSON
    static void extractFeatureFromJson(String stationJson, int channel) {

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(stationJson)) {
            return;
        }

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            JSONObject jsonObject = new JSONObject(stationJson);

            JSONArray feeds = jsonObject.getJSONArray("feeds");


            if(channel == 3) {
                // If this is camera channel and there is no new camera data, don't save new data
                if(!AppData.cameras.isEmpty() && AppData.cameras.get(0) != null &&
                        AppData.cameras.get(0).getAllPlates().size() == feeds.length()) {
                    return;
                }

                // reset all cameras' (only one for now) time and plates for adding new ones later
                try {
                    AppData.cameras.get(0).clearPlats();
                    AppData.cameras.get(0).clearTimes();
                } catch (Exception ignored) {}
            }


            for(int i = 0; i < feeds.length(); i++) {
                JSONObject obj2 = feeds.getJSONObject(i);

                // Loop through all feeds and save data based on channel
                switch (channel) {
                    case 1:
                        doChannel1(obj2);
                        break;
                    case 2:
                        doChannel2(obj2);
                        break;
                    case 3:
                        doChannel3(obj2);
                        break;
                    default:
                        Log.e(LOG_TAG, "Channel could not be handled");
                }

            }

        } catch (Exception e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e(LOG_TAG, "Problem parsing JSON results", e);
        }

    }


    // Meteorological station
    private static void doChannel1(JSONObject jsonObject) {
        int tempId;
        try {
            tempId = Integer.parseInt(jsonObject.getString("field1"));
        } catch (Exception e) {
            return;
        }
        Integer idInVector = null;

        // Loop through all stations and check if station with same ID exist
        for(int j = 0; j < AppData.meteorologicalStations.size(); j++) {
            if(AppData.meteorologicalStations.get(j).getId() == tempId) {
                idInVector = j;
                break;
            }
        }

        MeteorologicalStation tempMeteorologicalStation;

        if(idInVector == null) {
            // If station with same ID doesn't exist create new one
            tempMeteorologicalStation = new MeteorologicalStation(tempId);
        } else {
            // If it exist change it's data
            tempMeteorologicalStation = AppData.meteorologicalStations.get(idInVector);
        }

        // Try to get each data from JSON
        try {
            tempMeteorologicalStation.setAirTemperature(Double.parseDouble(jsonObject.getString("field2")));
        } catch (Exception ignored) {}
        try {
            tempMeteorologicalStation.setAirHumidity(Double.parseDouble(jsonObject.getString("field3")));
        } catch (Exception ignored) {}
        try {
            tempMeteorologicalStation.setAirQuality(Double.parseDouble(jsonObject.getString("field4")));
        } catch (Exception ignored) {}
        try {
            tempMeteorologicalStation.setCo2Level(Double.parseDouble(jsonObject.getString("field5")));
        } catch (Exception ignored) {}
        try {
            tempMeteorologicalStation.setCoLevel(Double.parseDouble(jsonObject.getString("field6")));
        } catch (Exception ignored) {}
        try {
            tempMeteorologicalStation.setDangerousGases(Integer.parseInt(jsonObject.getString("field7")) == 1);
        } catch (Exception ignored) {}


        // If station with same ID doesn't exist add it into the list
        if(idInVector == null) {
            AppData.meteorologicalStations.add(tempMeteorologicalStation);
        }

    }

    // Parking
    private static void doChannel2(JSONObject obj2) {
        Parking tempParking;
        if(AppData.parkings.isEmpty()) {
            // If parking with same ID doesn't exist create new one
            tempParking = new Parking(444, 3); // 444 is temp code for parking station
        } else {
            // If it exist change it's data
            tempParking = AppData.parkings.get(0);
        }

        // Try to get each data from JSON
        try {
            tempParking.getParkingSpot(0).setAvailable(Integer.parseInt(obj2.getString("field1")) == 0);
        } catch (Exception ignored) {}
        try {
            tempParking.getParkingSpot(1).setAvailable(Integer.parseInt(obj2.getString("field2")) == 0);
        } catch (Exception ignored) {}
        try {
            tempParking.getParkingSpot(2).setAvailable(Integer.parseInt(obj2.getString("field3")) == 0);
        } catch (Exception ignored) {}


        // If parking with same ID doesn't exist add it into the list
        if(AppData.parkings.isEmpty()) {
            AppData.parkings.add(tempParking);
        }

    }

    // Camera
    private static void doChannel3(JSONObject obj2) {
        Camera tempCamera;
        if(AppData.cameras.isEmpty()) {
            // If camera with same ID doesn't exist create new one
            tempCamera = new Camera(555); // 555 is temp code for camera
        } else {
            // If it exist change it's data
            tempCamera = AppData.cameras.get(0);
        }

        // Try to get each data from JSON
        try {
            tempCamera.addTime(obj2.getString("created_at"));
        } catch (Exception ignored) {}
        try {
            tempCamera.addPlate(obj2.getString("field1"));
        } catch (Exception ignored) {}


        // If camera with same ID doesn't exist add it into the list
        if(AppData.cameras.isEmpty()) {
            AppData.cameras.add(tempCamera);
        }

    }




}
