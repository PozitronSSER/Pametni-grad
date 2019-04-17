package com.sser.smartcity.smartcitypay;

import com.firebase.client.annotations.NotNull;

// Class for une user plate (vehicle)
class Plate {

    // Saves plate as string
    private String plate;

    // Plate needs to be passed in the constructor
    Plate(@NotNull String plate) {
        this.plate = plate;
    }


    /*
     * Public getters and setters (package-private in this case)
     */

    String getPlate() {
        return plate;
    }

    void setPlate(String plate) {
        this.plate = plate;
    }
}
