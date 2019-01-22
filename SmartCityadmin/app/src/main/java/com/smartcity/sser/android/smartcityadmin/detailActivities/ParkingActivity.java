package com.smartcity.sser.android.smartcityadmin.detailActivities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.smartcity.sser.android.smartcityadmin.Data;
import com.smartcity.sser.android.smartcityadmin.R;
import com.smartcity.sser.android.smartcityadmin.Stanica;

public class ParkingActivity extends AppCompatActivity {

    int stanicaPosition;
    Stanica stanica;

    ParkingAdapter parkingAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking);

        // Get position
        stanicaPosition = getIntent().getIntExtra("stanicaPosition", 0);

        setTitle(getResources().getString(R.string.parking) + " " + Data.sveStanice.elementAt(stanicaPosition).stanicaID);


        stanica = Data.sveStanice.elementAt(stanicaPosition);

        // Show all parking data
        ListView parkingListView = (ListView) findViewById(R.id.parking_list_view);

        parkingAdapter = new ParkingAdapter(ParkingActivity.this, stanica.parkirnaMjesta, stanicaPosition);

        parkingListView.setAdapter(parkingAdapter);

        parkingAdapter.notifyDataSetChanged();
    }
}
