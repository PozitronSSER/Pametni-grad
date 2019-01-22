package com.smartcity.sser.android.smartcityadmin.detailActivities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.smartcity.sser.android.smartcityadmin.Data;
import com.smartcity.sser.android.smartcityadmin.R;
import com.smartcity.sser.android.smartcityadmin.Stanica;

public class KameraActivity extends AppCompatActivity {

    int stanicaPosition;
    Stanica stanica;

    TextView auto, pjesak;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kamera);

        // Get position
        stanicaPosition = getIntent().getIntExtra("stanicaPosition", 0);

        setTitle(getResources().getString(R.string.kamera) + " " + Data.sveStanice.elementAt(stanicaPosition).stanicaID);


        stanica = Data.sveStanice.elementAt(stanicaPosition);

        auto = findViewById(R.id.auto);
        pjesak = findViewById(R.id.pjesak);

        // Write if Auto is vidljiv
        if(stanica.autoVidljiv) {
            auto.setText(getResources().getString(R.string.autoVidljiv));
        } else {
            auto.setText(getResources().getString(R.string.autoNijeVidljiv));
        }

        // Write if Pjesak is vidljiv
        if(stanica.pjesakVidljiv) {
            pjesak.setText(getResources().getString(R.string.pjesakVidljiv));
        } else {
            pjesak.setText(getResources().getString(R.string.pjesakNijeVidljiv));
        }

    }
}
