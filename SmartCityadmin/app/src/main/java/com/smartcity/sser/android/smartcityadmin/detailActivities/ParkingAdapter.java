package com.smartcity.sser.android.smartcityadmin.detailActivities;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.smartcity.sser.android.smartcityadmin.Data;
import com.smartcity.sser.android.smartcityadmin.ParkirnoMjesto;
import com.smartcity.sser.android.smartcityadmin.R;

import java.util.List;

public class ParkingAdapter extends ArrayAdapter<ParkirnoMjesto> {

    int idInVector;

    public ParkingAdapter(Activity context, List<ParkirnoMjesto> p, int idInVec) {
        super(context, 0, p);
        idInVector = idInVec;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        // For every item in List<ParkirnoMjesto> get existing one
        View listItemView = convertView;
        if(listItemView == null) {
            // If it doesn't exist create new one with parking_list_item layout
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.parking_list_item, parent, false);
        }

        TextView textViewStanica = (TextView) listItemView.findViewById(R.id.TVParking);


        // npr. "Parking 0: Slobodan"
        String writeMessage = getContext().getResources().getString(R.string.parking) + " " +
                Data.sveStanice.elementAt(idInVector).parkirnaMjesta.elementAt(position).parkirnoMjestoID + ": " +
                (Data.sveStanice.elementAt(idInVector).parkirnaMjesta.elementAt(position).stanjeParkirnogMjesta ?
                        getContext().getResources().getString(R.string.zauzet) :
                        getContext().getResources().getString(R.string.slobodan));


        // Write if parking is slobodan
        textViewStanica.setText(writeMessage);

        return listItemView;
    }

}
