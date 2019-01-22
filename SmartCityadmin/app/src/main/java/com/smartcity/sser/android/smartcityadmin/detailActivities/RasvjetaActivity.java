package com.smartcity.sser.android.smartcityadmin.detailActivities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.smartcity.sser.android.smartcityadmin.Data;
import com.smartcity.sser.android.smartcityadmin.R;
import com.smartcity.sser.android.smartcityadmin.Stanica;

public class RasvjetaActivity extends AppCompatActivity {

    int stanicaPosition;
    Stanica stanica;

    TextView auto, pjesak, svjetlost, pokret;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rasvjeta);

        // Get position
        stanicaPosition = getIntent().getIntExtra("stanicaPosition", 0);

        setTitle(getResources().getString(R.string.rasvjeta) + " " + Data.sveStanice.elementAt(stanicaPosition).stanicaID);


        stanica = Data.sveStanice.elementAt(stanicaPosition);

        auto = findViewById(R.id.auto);
        pjesak = findViewById(R.id.pjesak);
        svjetlost = findViewById(R.id.razina_svjetlosti);
        pokret = findViewById(R.id.pokret);

        // Write data TextViews
        svjetlost.setText(getResources().getString(R.string.razinaSvjetlost) + ": " + stanica.razinaSvijetlosti);

        if(stanica.pokretDetektiran) {
            pokret.setText(getResources().getString(R.string.pokretDetektiran));
        } else {
            pokret.setText(getResources().getString(R.string.pokretNijeDetektiran));
        }

        if(stanica.autoVidljiv) {
            auto.setText(getResources().getString(R.string.autoVidljiv));
        } else {
            auto.setText(getResources().getString(R.string.autoNijeVidljiv));
        }

        if(stanica.pjesakVidljiv) {
            pjesak.setText(getResources().getString(R.string.pjesakVidljiv));
        } else {
            pjesak.setText(getResources().getString(R.string.pjesakNijeVidljiv));
        }

    }
}
