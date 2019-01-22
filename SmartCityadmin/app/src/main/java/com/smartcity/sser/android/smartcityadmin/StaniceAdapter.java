package com.smartcity.sser.android.smartcityadmin;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class StaniceAdapter extends ArrayAdapter<Stanica> {

    public StaniceAdapter(Activity context, List<Stanica> s) {
        super(context, 0, s);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        // For every item in List<Stanica> get existing one
        View listItemView = convertView;
        if(listItemView == null) {
            // If it doesn't exist create new one with stanica_list_item layout
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.stanica_list_item, parent, false);
        }

        TextView textViewStanica = (TextView) listItemView.findViewById(R.id.TVStanica);
        textViewStanica.setText("ID: " + Data.sveStanice.elementAt(position).stanicaID);

        return listItemView;
    }

}
