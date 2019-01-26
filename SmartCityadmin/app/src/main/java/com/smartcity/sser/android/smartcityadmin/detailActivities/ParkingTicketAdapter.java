package com.smartcity.sser.android.smartcityadmin.detailActivities;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.smartcity.sser.android.smartcityadmin.Data;
import com.smartcity.sser.android.smartcityadmin.ParkingTicket;
import com.smartcity.sser.android.smartcityadmin.R;

import java.util.List;

public class ParkingTicketAdapter extends ArrayAdapter<ParkingTicket> {

    int idInVector;

    public ParkingTicketAdapter(Activity context, List<ParkingTicket> pt, int idInVec) {
        super(context, 0, pt);
        idInVector = idInVec;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        // For every item in List<ParkingTicket> get existing one
        View listItemView = convertView;
        if(listItemView == null) {
            // If it doesn't exist create new one with parking_ticket_list_item layout
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.parking_ticket_list_item, parent, false);
        }

        TextView parkingTicketID = listItemView.findViewById(R.id.TVParkingTicketID);
        TextView vrijemeUlaza = listItemView.findViewById(R.id.TVParkingTicketUlaz);
        TextView vrijemeIzlaza = listItemView.findViewById(R.id.TVParkingTicketIzlaz);
        TextView registration = listItemView.findViewById(R.id.TVParkingTicketRegistration);

        ParkingTicket parkingTicket = Data.sveStanice.elementAt(idInVector).parkingTickets.elementAt(position);

        // Write parking ticket data
        parkingTicketID.setText("ID: " + parkingTicket.parkingTicketID);
        vrijemeUlaza.setText(parkingTicket.vrijemeUlaza);
        vrijemeIzlaza.setText(parkingTicket.vrijemeIzlaza);
        registration.setText(parkingTicket.registracijaAuta);


        return listItemView;
    }

}
