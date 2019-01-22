package com.smartcity.sser.android.smartcityadmin;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class StaniceActivity extends AppCompatActivity {

    // TODO: add FAB for sending new information

    private static final String LOG_TAG = StaniceActivity.class.getName();

    String requestUrl1, requestUrl2, requestUrl3;

    StaniceAdapter staniceAdapter;

    ProgressBar progressBar;
    ImageView noInternetConnectionIV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stanice);

        setTitle(getResources().getString(R.string.stanice));

        // add query parameters in string requests
        requestUrl1 = JSONHandler.makeURL(Data.JSON_REQUEST_URL1);
        requestUrl2 = JSONHandler.makeURL(Data.JSON_REQUEST_URL2);
        requestUrl3 = JSONHandler.makeURL(Data.JSON_REQUEST_URL3);

        // init progress bar for loading (getting json)
        progressBar = findViewById(R.id.progress);
        progressBar.setVisibility(View.GONE);

        // init no internet connection watermark for displaying error if needed
        noInternetConnectionIV = findViewById(R.id.no_internet_connection_IV);

        if(Data.refresh) {
            refresh();
        } else {
            refreshLayout();
        }

    }

    // Get JSON responses from all channels
    public void refresh() {
        // If there is internet connection fetch data
        if(checkNetworkState()) {
            progressBar.setVisibility(View.VISIBLE);
            noInternetConnectionIV.setVisibility(View.GONE);
        } else { // If not, display error watermark, do not start loading
            noInternetConnectionIV.setVisibility(View.VISIBLE);
            Thread noNetConnectionThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {}

                    StaniceActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            noInternetConnectionIV.setVisibility(View.GONE);
                        }
                    });
                }
            });
            noNetConnectionThread.start();
            return;
        }

        Thread updateThread = new Thread(new Runnable() {
            @Override
            public void run() {

                for(int channel = 1; channel <= Data.numberOfChannels; channel++) {
                    // Create URL object
                    URL url = null;

                    try {
                        switch (channel) {
                            case 1:
                                url = new URL(requestUrl1);
                                break;
                            case 2:
                                url = new URL(requestUrl2);
                                break;
                            case 3:
                                url = new URL(requestUrl3);
                                break;
                            default:
                                Log.e(LOG_TAG, "Channel could not be handled");
                        }
                    }
                    catch (MalformedURLException e) {
                        Log.e(LOG_TAG, "Problem building the URL ", e);
                    }

                    // Perform HTTP request to the URL and receive a JSON response back
                    String jsonResponse = null;

                    try {
                        jsonResponse = JSONHandler.makeHttpRequest(url);
                    } catch (IOException e) {
                        Log.e(LOG_TAG, "Problem making the HTTP request.", e);
                    }

                    // "convert" JSON to data and save it
                    JSONHandler.extractFeatureFromJson(jsonResponse, channel);
                }

                StaniceActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout();
                    }
                });

                Data.refresh = false;

            }
        });
        updateThread.start();
    }

    // Refresh ListView
    public void refreshLayout() {

        progressBar.setVisibility(View.GONE);

        ListView staniceListView = (ListView) findViewById(R.id.stanica_list_view);

        staniceAdapter = new StaniceAdapter(StaniceActivity.this, Data.sveStanice);

        staniceListView.setAdapter(staniceAdapter);

        staniceAdapter.notifyDataSetChanged();


        staniceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
//                // Find the current stanica that was clicked on
//                Stanica currentStanica = staniceAdapter.getItem(position);

                // Open DetailsActivity, tell it position user has clicked on (position on ListView and Data.sveStanice)
                Intent intent = new Intent(StaniceActivity.this, DetailsActivity.class);
                intent.putExtra("stanicaPosition", position);
                Data.lastClickedOnStanicaPosition = position;
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Create menu base on main_menu layout
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh_option:
                refresh();
        }
        return super.onOptionsItemSelected(item);
    }

    // Returns true if there is connection to internet or false otherwise
    public boolean checkNetworkState() {

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        try {
            // Get details on the currently active default data network
            networkInfo = connMgr.getActiveNetworkInfo();
        } catch (NullPointerException e) {
            Log.e(LOG_TAG, "Cannot find active network connection");
        }

        // If there is a network connection, fetch data
        if(networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            // Otherwise, display error
            return false;
        }

    }

}
