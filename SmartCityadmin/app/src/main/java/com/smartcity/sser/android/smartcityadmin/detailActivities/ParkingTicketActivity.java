package com.smartcity.sser.android.smartcityadmin.detailActivities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.smartcity.sser.android.smartcityadmin.Data;
import com.smartcity.sser.android.smartcityadmin.R;
import com.smartcity.sser.android.smartcityadmin.Stanica;

public class ParkingTicketActivity extends AppCompatActivity {

    int stanicaPosition;
    Stanica stanica;

    ParkingTicketAdapter parkingTicketAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_ticket);

        // Get position
        stanicaPosition = getIntent().getIntExtra("stanicaPosition", 0);

        setTitle(getResources().getString(R.string.parkingTicket) + " " + Data.sveStanice.elementAt(stanicaPosition).stanicaID);


        stanica = Data.sveStanice.elementAt(stanicaPosition);

        // Show all parking data
        ListView parkingTicketListView = (ListView) findViewById(R.id.parking_ticket_list_view);

        parkingTicketAdapter = new ParkingTicketAdapter(ParkingTicketActivity.this, stanica.parkingTickets, stanicaPosition);

        parkingTicketListView.setAdapter(parkingTicketAdapter);

        parkingTicketAdapter.notifyDataSetChanged();
    }
}
