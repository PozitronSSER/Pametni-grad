package com.smartcity.sser.android.smartcityadmin;

public class ParkingTicket {

    public int parkingTicketID;

    public String vrijemeUlaza, vrijemeIzlaza;
    public String registracijaAuta;

    public ParkingTicket(int id) {

        parkingTicketID = id;

        vrijemeUlaza = null;
        vrijemeIzlaza = null;
        registracijaAuta = null;

    }

}
