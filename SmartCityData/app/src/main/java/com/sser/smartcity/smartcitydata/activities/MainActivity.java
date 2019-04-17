package com.sser.smartcity.smartcitydata.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.sser.smartcity.smartcitydata.R;
import com.sser.smartcity.smartcitydata.data.AppData;
import com.sser.smartcity.smartcitydata.networking.UpdateDataHandler;

// Main/first activity, user can choose category to see it's data
public class MainActivity extends AppCompatActivity {

    // Clickable views for opening each category
    View meteorologicalStationClickableView, streetLightClickableView,
            cameraClickableView, parkingClickableView, parkingTicketClickableView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Declare layout variables
        meteorologicalStationClickableView = findViewById(R.id.meteorological_station);
        streetLightClickableView = findViewById(R.id.street_light);
        cameraClickableView = findViewById(R.id.camera);
        parkingClickableView = findViewById(R.id.parking);
        parkingTicketClickableView = findViewById(R.id.parking_ticket);

        // Set on click listeners (open CategoryListActivity and store witch category has been clicked on)
        meteorologicalStationClickableView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppData.lastClickedCategoryTypeIndex = AppData.meteorologicalStationCategoryTypeIndex;
                openCategoryListActivity();
            }
        });
        streetLightClickableView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppData.lastClickedCategoryTypeIndex = AppData.streetLightCategoryTypeIndex;
                openCategoryListActivity();
            }
        });
        cameraClickableView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppData.lastClickedCategoryTypeIndex = AppData.cameraCategoryTypeIndex;
                openCategoryListActivity();
            }
        });
        parkingClickableView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppData.lastClickedCategoryTypeIndex = AppData.parkingCategoryTypeIndex;
                openCategoryListActivity();
            }
        });
        parkingTicketClickableView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppData.lastClickedCategoryTypeIndex = AppData.parkingTicketCategoryTypeIndex;
                openCategoryListActivity();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        // Last clicked category does not exist (MainActivity is not an category)
        AppData.lastClickedCategoryTypeIndex = AppData.noCategoryTypeDefaultIndex;


        // Handle resuming activity (activities have lot of same operations)
        AppData.resumeActivity(this, -1);

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
                        UpdateDataHandler.updateData(MainActivity.this);
                    }
                }).start();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    // Open CategoryListActivity for showing more details of an selected category
    private void openCategoryListActivity() {
        Intent intent = new Intent(MainActivity.this, CategoryListActivity.class);
        startActivity(intent);
    }
}
