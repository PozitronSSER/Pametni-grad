package com.sser.smartcity.smartcitydata.data;

// Class for storing one parking spot data (used only in Parking.class)
public class ParkingSpot {

    // Unique id of a parking spot
    private int id;

    // Is parking available
    private boolean available;

    // Package-private constructor for assigning just id
    ParkingSpot(int id) {
        this.id = id;

        // Available spot is default
        available = true;
    }

    // Public constructor for assigning all data
    public ParkingSpot(int id, boolean available) {
        this.id = id;
        this.available = available;
    }



    /*
     * Public getters and setters
     */

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
