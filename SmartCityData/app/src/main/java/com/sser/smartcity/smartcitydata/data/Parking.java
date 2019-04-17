package com.sser.smartcity.smartcitydata.data;

import java.util.ArrayList;

// Class for storing one parking data
public class Parking {

    // Unique id of a parking
    private int id;

    // List of parking spots on the parking
    private ArrayList<ParkingSpot> parkingSpots;

    // Constructor
    public Parking(int id, int numberOfParkingSpots) {
        this.id = id;

        // Create new ArrayList with given number of spots
        parkingSpots = new ArrayList<>();

        for(int i = 0; i < numberOfParkingSpots; i++) {
            parkingSpots.add(new ParkingSpot(i));
        }
    }


    // Returns total number of parking spots
    public int getNumberOfParkingSpots() {
        if(parkingSpots == null) {
            return 0;
        }
        return parkingSpots.size();
    }

    // Returns number of available parking spots (just count them)
    public int getAvailableParkingSpotsCount() {
        if(parkingSpots == null) {
            return 0;
        }
        int counter = 0;
        for (ParkingSpot ps : parkingSpots) {
            if(ps.isAvailable()) {
                counter++;
            }
        }
        return counter;
    }


    /*
     * Public getters and setters (and add method)
     */

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ParkingSpot getParkingSpot(int index) {
        return parkingSpots.get(index);
    }

    public void addParkingSpot(ParkingSpot parkingSpot) {
        this.parkingSpots.add(parkingSpot);
    }
}
