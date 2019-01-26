package com.smartcity.sser.android.smartcityadmin.detailActivities;

import android.annotation.SuppressLint;
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

    boolean radi = true;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meteoroloska_stanica);

        stanicaPosition = getIntent().getIntExtra("stanicaPosition", 0);

        setTitle(getResources().getString(R.string.meteoroloskaStanica) + " " + Data.sveStanice.elementAt(stanicaPosition).stanicaID);





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




    }

    @Override
    protected void onResume() {
        super.onResume();

        radi = true;

        Thread refreshThread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                while(radi) {
                    try {
                        MeteoroloskaStanicaActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                stanica = Data.sveStanice.elementAt(stanicaPosition);

                                tempZrak = findViewById(R.id.temperatura_zraka);
                                vlaga = findViewById(R.id.vlaga_zraka);
                                brzinaVjetra = findViewById(R.id.brzina_vjetra);
                                smjerVjetra = findViewById(R.id.smjer_vjetra);
                                kolicinaPadalina = findViewById(R.id.kolicina_padalina);
                                tlakZraka = findViewById(R.id.tlak_zraka);

                                tempZrak.setText(getResources().getString(R.string.temperaturaZraka) + ": " + stanica.temperaturaZraka);
                                vlaga.setText(getResources().getString(R.string.vlagaZraka) + ": " + stanica.vlagaZraka);
                                brzinaVjetra.setText(getResources().getString(R.string.kvalitetaZraka) + ": " + stanica.kvalitetaZraka);
                                smjerVjetra.setText(getResources().getString(R.string.razinaCO2) + ": " + stanica.razinaCO2);
                                kolicinaPadalina.setText(getResources().getString(R.string.razinaCO) + ": " + stanica.razinaCO);
                                tlakZraka.setText((stanica.opasniPlinovi ? getResources().getString(R.string.opasniPlinoviTrue) :
                                        getResources().getString(R.string.opasniPlinoviFalse)));
                            }
                        });

                    } catch(Exception e) {

                    }

                    try {
                        Thread.sleep(5000);
                    } catch (Exception e) {

                    }
                }
            }
        });
        refreshThread1.start();
    }

    @Override
    protected void onStop() {
        super.onStop();

        radi = false;
    }
}
