package com.sser.smartcity.smartcitydata.data;

import com.sser.smartcity.smartcitydata.activities.CameraActivity;

import java.util.ArrayList;

// Class for storing one camera data
public class Camera {

    // Unique id of a camera
    private int id;

    // List of car plates camera has seen
    private ArrayList<String> plates;
    // List of times camera has seen this plates (must be equally long)
    private ArrayList<String> times;

    // Constructor
    public Camera(int id) {
        this.id = id;

        // Create new ArrayLists
        plates = new ArrayList<>();
        times = new ArrayList<>();
    }


    /*
     * Public get/set methods (and add and clear methods)
     */

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlate(int index) {
        return plates.get(index);
    }

    public ArrayList<String> getAllPlates() {
        return this.plates;
    }

    public void addPlate(String plate) {
        this.plates.add(plate);
        notifyCameraDataAdapter();
    }

    public void setPlate(int index, String plate) {
        this.plates.set(index, plate);
        notifyCameraDataAdapter();
    }

    public void clearPlats() {
        this.plates.clear();
        notifyCameraDataAdapter();
    }


    public String getTime(int index) {
        return times.get(index);
    }

    public void addTime(String time) {
        this.times.add(time.replace("T", " "));
        notifyCameraDataAdapter();
    }

    public void setTime(int index, String time) {
        this.times.set(index, time);
        notifyCameraDataAdapter();
    }

    public void clearTimes() {
        this.times.clear();
        notifyCameraDataAdapter();
    }


    // TODO: make this process smarter (look only for current camera's JSON feed)
    // Notify camera data adapter (from UI thread) that camera data has been changed (crash bug fix)
    private static void notifyCameraDataAdapter() {
        try {
            AppData.currentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        CameraActivity.cameraDataAdapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
