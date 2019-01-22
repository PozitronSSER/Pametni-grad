package com.smartcity.sser.android.smartcityadmin;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

public class JSONHandler {

    private static final String LOG_TAG = JSONHandler.class.getName();

    // add query parameters in string request
    public static String makeURL(String request) {

        Uri baseUri = Uri.parse(request);
        Uri.Builder uriBuilder = baseUri.buildUpon();

//        uriBuilder.appendQueryParameter("results", "2");

        return uriBuilder.toString();
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    public static String makeHttpRequest(URL url) throws IOException {
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
        } catch (IOException e) {
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


    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    public static String readFromStream(InputStream inputStream) throws IOException{
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
    public static void extractFeatureFromJson(String stanicaJSON, int channel) {
//        Log.e(LOG_TAG, stanicaJSON);

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(stanicaJSON)) {
            return;
        }

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            JSONObject jsonObject = new JSONObject(stanicaJSON);

            JSONArray feeds = jsonObject.getJSONArray("feeds");
            for(int i = 0; i < feeds.length(); i++) {
                JSONObject obj2 = feeds.getJSONObject(i);

                // Loop through all feeds and save data based on channel
                if(channel == 1) {
                    doChannel1(obj2);
                } else if(channel == 2) {
                    doChannel2(obj2);
                } else if(channel == 3) {
                    doChannel3(obj2);
                }

            }

        } catch (Exception e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e(LOG_TAG, "Problem parsing JSON results", e);
        }

    }



    public static void doChannel1(JSONObject obj2) throws JSONException {
        Integer tempStanicaID;
        try {
            tempStanicaID = Integer.parseInt(obj2.getString("field1"));
        } catch (Exception e) {
            return;
        }
        Integer idInVector = null;

        // Loop through all Stanica and check if stanica with same ID exist
        for(int j = 0; j < Data.sveStanice.size(); j++) { // TODO: optimize, implement DB
            if(Data.sveStanice.elementAt(j).stanicaID == tempStanicaID) {
                idInVector = j;
                break;
            }
        }

        Stanica tempStanica;

        // If Stanica with same ID doesn't exist create new one
        if(idInVector == null) {
            tempStanica = new Stanica(tempStanicaID);
        } else { // If it exist change it's data
            tempStanica = Data.sveStanice.elementAt(idInVector);
        }

        try {
            tempStanica.temperaturaZraka = Double.parseDouble(obj2.getString("field2"));
        } catch (Exception e) {}
        try {
            tempStanica.vlagaZraka = Double.parseDouble(obj2.getString("field3"));
        } catch (Exception e) {}
        try {
            tempStanica.brzinaVjetra = Double.parseDouble(obj2.getString("field4"));
        } catch (Exception e) {}
        try {
            tempStanica.smjerVjetra = Double.parseDouble(obj2.getString("field5"));
        } catch (Exception e) {}
        try {
            tempStanica.kolicinaPadalina = Double.parseDouble(obj2.getString("field6"));
        } catch (Exception e) {}
        try {
            tempStanica.tlakZraka = Double.parseDouble(obj2.getString("field7"));
        } catch (Exception e) {}


        if(idInVector == null) {
            Data.sveStanice.add(tempStanica);
        }

    }

    public static void doChannel2(JSONObject obj2) throws JSONException {
        Integer tempStanicaID;
        try {
            tempStanicaID = Integer.parseInt(obj2.getString("field1"));
        } catch (Exception e) {
            return;
        }
        Integer idInVector = null;

        // Loop through all Stanica and check if stanica with same ID exist
        for(int j = 0; j < Data.sveStanice.size(); j++) { // TODO: optimize, implement DB
            if(Data.sveStanice.elementAt(j).stanicaID == tempStanicaID) {
                idInVector = j;
                break;
            }
        }

        Stanica tempStanica;

        // If Stanica with same ID doesn't exist create new one
        if(idInVector == null) {
            tempStanica = new Stanica(tempStanicaID);
        } else { // If it exist change it's data
            tempStanica = Data.sveStanice.elementAt(idInVector);
        }

        try {
            tempStanica.razinaSvijetlosti = Double.parseDouble(obj2.getString("field2"));
        } catch (Exception e) {}
        try {
            tempStanica.pokretDetektiran = Double.parseDouble(obj2.getString("field3")) == 1;
        } catch (Exception e) {}
        try {
            tempStanica.autoVidljiv = Double.parseDouble(obj2.getString("field4")) == 1;
        } catch (Exception e) {}
        try {
            tempStanica.pjesakVidljiv = Double.parseDouble(obj2.getString("field5")) == 1;
        } catch (Exception e) {}
        try {
            tempStanica.TVOC = Double.parseDouble(obj2.getString("field6"));
        } catch (Exception e) {}
        try {
            tempStanica.eCO2 = Double.parseDouble(obj2.getString("field7"));
        } catch (Exception e) {}


        if(idInVector == null) {
            Data.sveStanice.add(tempStanica);
        }

    }

    public static void doChannel3(JSONObject obj2) throws JSONException {
        Integer tempStanicaID;
        try {
            tempStanicaID = Integer.parseInt(obj2.getString("field1"));
        } catch (Exception e) {
            return;
        }
        Integer idInVector = null;

        // Loop through all Stanica and check if stanica with same ID exist
        for(int j = 0; j < Data.sveStanice.size(); j++) {
            if(Data.sveStanice.elementAt(j).stanicaID == tempStanicaID) {
                idInVector = j;
                break;
            }
        }

        Stanica tempStanica;

        // If Stanica with same ID doesn't exist create new one
        if(idInVector == null) {
            tempStanica = new Stanica(tempStanicaID);
        } else { // If it exist change it's data
            tempStanica = Data.sveStanice.elementAt(idInVector);
        }

        Integer tempParkirnoMjestoId = null;

        Integer idOfParkirnoMjesto = null;
        Boolean tempStanjeParkirnogMjesta = null;

        try {
            // Get parkirno mjesto Id
            tempParkirnoMjestoId = Integer.parseInt(obj2.getString("field2"));

            // Check if it already exist
            for(int j = 0; j < Data.sveStanice.elementAt(idInVector).parkirnaMjesta.size(); j++) { // TODO: optimize
                if(Data.sveStanice.elementAt(idInVector).parkirnaMjesta.elementAt(j).parkirnoMjestoID == tempParkirnoMjestoId) {
                    idOfParkirnoMjesto = j;
                    break;
                }
            }

        } catch (Exception e) {}
        try {
            // Get Stanje parkirnog mjesta
            tempStanjeParkirnogMjesta = Integer.parseInt(obj2.getString("field3")) == 1;
        } catch (Exception e) {}

        // If parkirno data is valid save it
        if(tempParkirnoMjestoId != null && tempStanjeParkirnogMjesta != null) {
            // If it is saved with same ID, change data, if not create new one
            if(idOfParkirnoMjesto == null) {
                ParkirnoMjesto tempParkirnoMjesto = new ParkirnoMjesto(tempParkirnoMjestoId);
                tempParkirnoMjesto.stanjeParkirnogMjesta = tempStanjeParkirnogMjesta;
                tempStanica.parkirnaMjesta.add(tempParkirnoMjesto);
            } else {
                tempStanica.parkirnaMjesta.elementAt(idOfParkirnoMjesto).stanjeParkirnogMjesta = tempStanjeParkirnogMjesta;
            }
        }


        Integer tempTicketID = null;
        String tempVrijemeUlaza = null;
        String tempVrijemeIzlaza = null;
        String tempRegistracija = null;

        Integer idOfParkingTicket = null;

        try {
            // Get Ticket ID
            tempTicketID = Integer.parseInt(obj2.getString("field4"));

            // Check if it already exist
            for(int j = 0; j < Data.sveStanice.elementAt(idInVector).parkingTickets.size(); j++) { // TODO: optimize
                if(Data.sveStanice.elementAt(idInVector).parkingTickets.elementAt(j).parkingTicketID == tempTicketID) {
                    idOfParkingTicket = j;
                    break;
                }
            }
        } catch (Exception e) {}
        try {
            tempVrijemeUlaza = obj2.getString("field5");
        } catch (Exception e) {}
        try {
            tempVrijemeIzlaza = obj2.getString("field6");
        } catch (Exception e) {}
        try {
            tempRegistracija = obj2.getString("field7");
        } catch (Exception e) {}

        // If ticket with same ID exist update it's data that is valid
        if(idOfParkingTicket != null) {
            if(tempVrijemeUlaza != null) {
                Data.sveStanice.elementAt(idInVector).parkingTickets.elementAt(idOfParkingTicket).vrijemeUlaza = tempVrijemeUlaza;
            }
            if(tempVrijemeIzlaza != null) {
                Data.sveStanice.elementAt(idInVector).parkingTickets.elementAt(idOfParkingTicket).vrijemeIzlaza = tempVrijemeIzlaza;
            }
            if(tempRegistracija != null) {
                Data.sveStanice.elementAt(idInVector).parkingTickets.elementAt(idOfParkingTicket).registracijaAuta = tempRegistracija;
            }
        } else if(tempTicketID != null) { // If not add new one, if ID is valid
            ParkingTicket tempPT = new ParkingTicket(tempTicketID);
            tempPT.vrijemeUlaza = tempVrijemeUlaza;
            tempPT.vrijemeIzlaza = tempVrijemeIzlaza;
            tempPT.registracijaAuta = tempRegistracija;

            Data.sveStanice.elementAt(idInVector).parkingTickets.add(tempPT);
        }


        if(idInVector == null) {
            Data.sveStanice.add(tempStanica);
        }

    }



}
