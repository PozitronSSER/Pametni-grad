package com.sser.smartcity.smartcitydata.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.sser.smartcity.smartcitydata.CategoryListAdapters.CamerasAdapter;
import com.sser.smartcity.smartcitydata.CategoryListAdapters.MeteorologicalStationAdapter;
import com.sser.smartcity.smartcitydata.CategoryListAdapters.ParkingAdapter;
import com.sser.smartcity.smartcitydata.R;
import com.sser.smartcity.smartcitydata.data.AppData;
import com.sser.smartcity.smartcitydata.networking.UpdateDataHandler;

// Activity for seeing all stations if each category (user can then open each station and see it's data)
public class CategoryListActivity extends AppCompatActivity {

    // Log tag for log messages
    private static final String LOG_TAG = CategoryListActivity.class.getName();

    // List of all stations in this category
    ListView categoryDataListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);

        // Declare layout variables
        categoryDataListView = findViewById(R.id.category_list_view);

        // TODO: when we get new data, this list will not refresh

        // Depending on witch category is opened, declare list view adapters and sets activity title
        switch (AppData.lastClickedCategoryTypeIndex) {
            case AppData.meteorologicalStationCategoryTypeIndex: // Meteorological station category
                setTitle(R.string.meteorological_station);

                // Set new adapter
                categoryDataListView.setAdapter(new MeteorologicalStationAdapter(this, AppData.meteorologicalStations));

                break;
            case AppData.streetLightCategoryTypeIndex: // Street light category, for now there is no data for this category
                setTitle(R.string.street_light);
                break;
            case AppData.cameraCategoryTypeIndex: // Camera category
                setTitle(R.string.camera);

                // Set new adapter
                categoryDataListView.setAdapter(new CamerasAdapter(this, AppData.cameras));

                break;
            case AppData.parkingCategoryTypeIndex: // Parking category
                setTitle(R.string.parking);

                // Set new adapter
                categoryDataListView.setAdapter(new ParkingAdapter(this, AppData.parkings));

                break;
            case AppData.parkingTicketCategoryTypeIndex: // Parking ticket category, for now there is no data for this category
                setTitle(R.string.parking_ticket);
                break;
            default:
                // Category not valid, show error (in logcat, not to the user)
                Log.e(LOG_TAG, "Data list category not valid");
        }

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
                        UpdateDataHandler.updateData(CategoryListActivity.this);
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
