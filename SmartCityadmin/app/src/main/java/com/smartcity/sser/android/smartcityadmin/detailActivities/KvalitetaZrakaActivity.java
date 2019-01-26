package com.smartcity.sser.android.smartcityadmin.detailActivities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.smartcity.sser.android.smartcityadmin.Data;
import com.smartcity.sser.android.smartcityadmin.R;
import com.smartcity.sser.android.smartcityadmin.Stanica;

public class KvalitetaZrakaActivity extends AppCompatActivity {

    int stanicaPosition;
    Stanica stanica;

    TextView tvoc, eco2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kvaliteta_zraka);

        stanicaPosition = getIntent().getIntExtra("stanicaPosition", 0);

        setTitle(getResources().getString(R.string.kvalitetaZraka) + " " + Data.sveStanice.elementAt(stanicaPosition).stanicaID);


        stanica = Data.sveStanice.elementAt(stanicaPosition);

        tvoc = findViewById(R.id.TVOC);
        eco2 = findViewById(R.id.eCO2);

        tvoc.setText(getResources().getString(R.string.TVOC) + ": " + stanica.TVOC);
        eco2.setText(getResources().getString(R.string.eCO2) + ": " + stanica.eCO2);

    }
}
