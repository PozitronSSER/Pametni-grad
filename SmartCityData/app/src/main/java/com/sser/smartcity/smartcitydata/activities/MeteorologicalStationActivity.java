package com.sser.smartcity.smartcitydata.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.sser.smartcity.smartcitydata.R;
import com.sser.smartcity.smartcitydata.data.AppData;
import com.sser.smartcity.smartcitydata.networking.UpdateDataHandler;

// Activity for showing all data of selected meteorological station
public class MeteorologicalStationActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meteorological_station);

        // For title, set opened meteorological station name
        setTitle(R.string.only_meteorological_station_name);

        // Show back (up) button in the menu
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        } catch (Exception ignored) {}
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Handle resuming activity (activities have lot of same operations)
        AppData.resumeActivity(this, AppData.lastClickedStationListIndex);

    }

    // Attach custom options menu on the activity (for the refresh button)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.refresh_menu, menu);
        return true;
    }

    // Handle click on each of the options menu buttons (items)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh_option: // Refresh button
                // Show loading progress bar
                View loadingProgressBar = findViewById(R.id.progress);
                loadingProgressBar.setVisibility(View.VISIBLE);

                // Update data from the internet
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        UpdateDataHandler.updateData(MeteorologicalStationActivity.this);
                    }
                }).start();
                return true;
            case android.R.id.home: // Back button
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
