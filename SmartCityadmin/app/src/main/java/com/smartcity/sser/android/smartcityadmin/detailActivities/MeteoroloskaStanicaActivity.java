package com.smartcity.sser.android.smartcityadmin.detailActivities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.smartcity.sser.android.smartcityadmin.Data;
import com.smartcity.sser.android.smartcityadmin.R;
import com.smartcity.sser.android.smartcityadmin.Stanica;

public class MeteoroloskaStanicaActivity extends AppCompatActivity {

    int stanicaPosition;
    Stanica stanica;

    TextView tempZrak, vlaga, brzinaVjetra, smjerVjetra, kolicinaPadalina, tlakZraka;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meteoroloska_stanica);

        stanicaPosition = getIntent().getIntExtra("stanicaPosition", 0);

        setTitle(getResources().getString(R.string.meteoroloskaStanica) + " " + Data.sveStanice.elementAt(stanicaPosition).stanicaID);



        stanica = Data.sveStanice.elementAt(stanicaPosition);

        tempZrak = findViewById(R.id.temperatura_zraka);
        vlaga = findViewById(R.id.vlaga_zraka);
        brzinaVjetra = findViewById(R.id.brzina_vjetra);
        smjerVjetra = findViewById(R.id.smjer_vjetra);
        kolicinaPadalina = findViewById(R.id.kolicina_padalina);
        tlakZraka = findViewById(R.id.tlak_zraka);

//        if(stanica.temperaturaZraka == null) {
//            tempZrak.setVisibility(View.GONE);
//        }
//        if(stanica.vlagaZraka == null) {
//            vlaga.setVisibility(View.GONE);
//        }
//        if(stanica.brzinaVjetra == null) {
//            brzinaVjetra.setVisibility(View.GONE);
//        }
//        if(stanica.smjerVjetra == null) {
//            smjerVjetra.setVisibility(View.GONE);
//        }
//        if(stanica.kolicinaPadalina == null) {
//            kolicinaPadalina.setVisibility(View.GONE);
//        }
//        if(stanica.tlakZraka == null) {
//            tlakZraka.setVisibility(View.GONE);
//        }

        tempZrak.setText(getResources().getString(R.string.temperaturaZraka) + ": " + stanica.temperaturaZraka);
        vlaga.setText(getResources().getString(R.string.vlagaZraka) + ": " + stanica.vlagaZraka);
        brzinaVjetra.setText(getResources().getString(R.string.brzinaVjetra) + ": " + stanica.brzinaVjetra);
        smjerVjetra.setText(getResources().getString(R.string.smjerVjetra) + ": " + stanica.smjerVjetra);
        kolicinaPadalina.setText(getResources().getString(R.string.kolicinaPadalina) + ": " + stanica.kolicinaPadalina);
        tlakZraka.setText(getResources().getString(R.string.tlakZraka) + ": " + stanica.tlakZraka);


    }
}
