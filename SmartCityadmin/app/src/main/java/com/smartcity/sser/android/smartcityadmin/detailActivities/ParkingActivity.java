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

    boolean radi = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking);

        // Get position
        stanicaPosition = getIntent().getIntExtra("stanicaPosition", 0);

        setTitle(getResources().getString(R.string.parking) + " " + Data.sveStanice.elementAt(stanicaPosition).stanicaID);



    }

    @Override
    protected void onResume() {
        super.onResume();

        radi = true;

        Thread refreshThread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                while(radi) {
                    try {
                        ParkingActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                stanica = Data.sveStanice.elementAt(stanicaPosition);

                                // Show all parking data
                                ListView parkingListView = (ListView) findViewById(R.id.parking_list_view);

                                parkingAdapter = new ParkingAdapter(ParkingActivity.this, stanica.parkirnaMjesta, stanicaPosition);

                                parkingListView.setAdapter(parkingAdapter);

                                parkingAdapter.notifyDataSetChanged();
                            }
                        });

                    } catch (Exception e) {

                    }

                    try {
                        Thread.sleep(5000);
                    } catch (Exception e) {

                    }
                }
            }
        });
        refreshThread2.start();

    }

    @Override
    protected void onStop() {
        super.onStop();

        radi = false;
    }
}
