package com.smartcity.sser.android.smartcityadmin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.smartcity.sser.android.smartcityadmin.detailActivities.KameraActivity;
import com.smartcity.sser.android.smartcityadmin.detailActivities.KvalitetaZrakaActivity;
import com.smartcity.sser.android.smartcityadmin.detailActivities.MeteoroloskaStanicaActivity;
import com.smartcity.sser.android.smartcityadmin.detailActivities.ParkingActivity;
import com.smartcity.sser.android.smartcityadmin.detailActivities.ParkingTicketActivity;
import com.smartcity.sser.android.smartcityadmin.detailActivities.RasvjetaActivity;


public class DetailsActivity extends AppCompatActivity {

    int stanicaPosition;

    TextView meteoStanica, kvalZraka, parking, rasvjeta, kamera, parkingTicket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // Get position of this Stanica
        stanicaPosition = getIntent().getIntExtra("stanicaPosition", -1);

        if(stanicaPosition == -1) {
            stanicaPosition = Data.lastClickedOnStanicaPosition;
        }

        setTitle(getResources().getString(R.string.stanica) + " " + Data.sveStanice.elementAt(stanicaPosition).stanicaID);

        meteoStanica = findViewById(R.id.meteoroloska_stanica);
        kvalZraka = findViewById(R.id.kvaliteta_zraka);
        parking = findViewById(R.id.parking);
        rasvjeta = findViewById(R.id.rasvjeta);
        kamera = findViewById(R.id.kamera);
        parkingTicket = findViewById(R.id.parking_ticket);


        // If it is cliced, open their activity
        meteoStanica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailsActivity.this, MeteoroloskaStanicaActivity.class);
                intent.putExtra("stanicaPosition", stanicaPosition);
                startActivity(intent);
            }
        });
        kvalZraka.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailsActivity.this, KvalitetaZrakaActivity.class);
                intent.putExtra("stanicaPosition", stanicaPosition);
                startActivity(intent);
            }
        });
        parking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailsActivity.this, ParkingActivity.class);
                intent.putExtra("stanicaPosition", stanicaPosition);
                startActivity(intent);
            }
        });
        rasvjeta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailsActivity.this, RasvjetaActivity.class);
                intent.putExtra("stanicaPosition", stanicaPosition);
                startActivity(intent);
            }
        });
        kamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailsActivity.this, KameraActivity.class);
                intent.putExtra("stanicaPosition", stanicaPosition);
                startActivity(intent);
            }
        });
        parkingTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailsActivity.this, ParkingTicketActivity.class);
                intent.putExtra("stanicaPosition", stanicaPosition);
                startActivity(intent);
            }
        });


        Stanica stanica = Data.sveStanice.elementAt(stanicaPosition);

        // If we don't have data of it, don't show it
        if(!stanica.jeliMeteroloskaStanica()) {
            meteoStanica.setVisibility(View.GONE);
        }
        if(!stanica.jeliKvalitetaZraka()) {
            kvalZraka.setVisibility(View.GONE);
        }
        if(!stanica.jeliParking()) {
            parking.setVisibility(View.GONE);
        }
        // Rasvjeta data is Kamera data + something more
        // If rasvjeta data is valid don't show Kamera
        if(!stanica.jeliRasvjeta()) {
            rasvjeta.setVisibility(View.GONE);
        } else {
            kamera.setVisibility(View.GONE);
        }
        if(!stanica.jeliKamera()) {
            kamera.setVisibility(View.GONE);
        }
        if(!stanica.jeliParking()) {
            parking.setVisibility(View.GONE);
        }
        if(!stanica.jeliParkirnaKarta()) {
            parkingTicket.setVisibility(View.GONE);
        }


    }


}
