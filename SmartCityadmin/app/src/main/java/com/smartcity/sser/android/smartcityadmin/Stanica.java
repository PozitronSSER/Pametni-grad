package com.smartcity.sser.android.smartcityadmin;

import java.util.Vector;

public class Stanica {

    public int stanicaID;

    // Channel 1
    public Double temperaturaZraka, vlagaZraka, brzinaVjetra, smjerVjetra, kolicinaPadalina, tlakZraka;

    // Channel 2
    public Double razinaSvijetlosti, TVOC, eCO2;
    public Boolean pokretDetektiran, autoVidljiv, pjesakVidljiv;

    // Channel 3
    public Vector<ParkirnoMjesto> parkirnaMjesta = new Vector<>();
    public Vector<ParkingTicket> parkingTickets = new Vector<>();

    // Stanica must have id declared
    public Stanica(int id) {

        stanicaID = id;

        // Set everything to null se we can check if it has new data or not
        // Maybe not needed
        temperaturaZraka = null;
        vlagaZraka = null;
        brzinaVjetra = null;
        smjerVjetra = null;
        kolicinaPadalina = null;
        tlakZraka = null;
        razinaSvijetlosti = null;
        TVOC = null;
        eCO2 = null;
        pokretDetektiran = null;
        autoVidljiv = null;
        pjesakVidljiv = null;
    }


    // Check if it has valid MeteroloskaStanica data
    public boolean jeliMeteroloskaStanica() {
        return (temperaturaZraka != null || vlagaZraka != null || brzinaVjetra != null || smjerVjetra != null ||
        kolicinaPadalina != null || tlakZraka != null);
    }

    // Check if it has valid KvalitetaZraka data
    public boolean jeliKvalitetaZraka() {
        return (TVOC != null || eCO2 != null);
    }

    // Check if it has valid Rasvjeta data
    public boolean jeliRasvjeta() {
        return (razinaSvijetlosti != null || pokretDetektiran != null);
    }

    // Check if it has valid Kamera data
    public boolean jeliKamera() {
        return (autoVidljiv != null || pjesakVidljiv != null);
    }

    // Check if it has valid Parking data
    public boolean jeliParking() {
        return (parkirnaMjesta.size() > 0);
    }

    // Check if it has valid ParkirnaKarta data
    public boolean jeliParkirnaKarta() {
        return (parkingTickets.size() > 0);
    }



}
