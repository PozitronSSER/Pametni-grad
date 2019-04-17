package com.sser.smartcity.smartcitydata.CategoryListAdapters;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.sser.smartcity.smartcitydata.GoogleMapsHandler;
import com.sser.smartcity.smartcitydata.R;
import com.sser.smartcity.smartcitydata.activities.ParkingActivity;
import com.sser.smartcity.smartcitydata.data.AppData;
import com.sser.smartcity.smartcitydata.data.Parking;

import java.util.ArrayList;

// Adapter for list of parkings in the CategoryListActivity
public class ParkingAdapter extends ArrayAdapter<Parking> {

    // Public constructor
    public ParkingAdapter(Activity context, ArrayList<Parking> station) {
        super(context, 0, station);
    }

    // This is called on creation of every list item (old list item can be reused in this process)
    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        // Try to recycle old view
        View listItemView = convertView;
        if(listItemView == null) {
            // If it doesn't exist create new one with list_item_category_data layout
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_category_data, parent, false);
        }

        // Assign names to evey parking (for now that is just one parking)
        TextView parkingTV = listItemView.findViewById(R.id.station_name_text_view);
        parkingTV.setText(R.string.only_parking_name);

        // Set on each item click listener (open activity with details)
        View parentLayout = listItemView.findViewById(R.id.parent_layout);
        parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Remember what station was clicked on (for getting it's data later)
                AppData.lastClickedStationListIndex = position;

                // Open activity with all data
                Intent intent = new Intent(getContext(), ParkingActivity.class);
                getContext().startActivity(intent);
            }
        });

        // Set click listener for opening google maps
        View openGoogleMapsClickableView = listItemView.findViewById(R.id.open_google_maps_clickable_view);
        openGoogleMapsClickableView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open google map
                GoogleMapsHandler.openGoogleMaps(AppData.currentActivity, GoogleMapsHandler.parkingLat,
                        GoogleMapsHandler.parkingLong, AppData.readResource(R.string.only_parking_name, getContext()));
            }
        });

        // Temporary get current parking
        Parking tempParking = AppData.parkings.get(position);

        // Set available parking number text
        TextView stationFillTextView = listItemView.findViewById(R.id.station_fill_text_view);
        stationFillTextView.setText(String.valueOf(tempParking.getAvailableParkingSpotsCount()) + "/" + String.valueOf(tempParking.getNumberOfParkingSpots()));

        // Return view
        return listItemView;
    }

}
