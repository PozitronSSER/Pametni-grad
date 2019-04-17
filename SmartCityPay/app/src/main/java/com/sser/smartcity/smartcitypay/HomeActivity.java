package com.sser.smartcity.smartcitypay;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


// First activity after user logs in
public class HomeActivity extends AppCompatActivity {

    // Stores current plateAdapter for updating it from static methods (changing it's data)
    private static PlateAdapter plateAdapter = null;

    // Parent layout for up-scroll refresh feature
    SwipeRefreshLayout swipeRefreshParentLayout;

    // Scroll view, nested to work with swipeRefreshParentLayout
    NestedScrollView parentScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Set activity title to be user's name
        setTitle(AppData.firebaseUser.getDisplayName());

        // Setup layout for layout for swipe refreshing data
        swipeRefreshParentLayout = findViewById(R.id.swipe_refresh_parent_layout);
        swipeRefreshParentLayout.setColorSchemeResources(R.color.loadingColor);
        swipeRefreshParentLayout.setEnabled(true);
        swipeRefreshParentLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshWholeLayout();
                changeRefreshAnimationState(true);
            }
        });

        // Setup button to add money to the account
        View addToTheAccountButton = findViewById(R.id.add_payment_to_the_account_button);
        addToTheAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAccountPaymentDialog();
            }
        });

        // Set plateAdapter for displaying all user plates
        ListView userPlatesLV = findViewById(R.id.user_plates_list_view);
        plateAdapter = new PlateAdapter(this, AppData.userPlates);
        userPlatesLV.setAdapter(plateAdapter);

        // Sets constant height of the list view
        setListViewHeightBasedOnChildren(userPlatesLV);

        // Setup button for adding new plate
        View addPlateClickableIV = findViewById(R.id.add_plate_clickable_image_view);
        addPlateClickableIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddPlateDialog();
            }
        });

        // Initially, scroll to the top (in case resizing list view scrolls to the middle)
        parentScrollView = findViewById(R.id.home_activity_parent_scroll_view);
        parentScrollView.smoothScrollTo(0, 0);
        // Make scroll refresh possible only when ScrollView is scrolled to the top
        parentScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                int scrollY = parentScrollView.getScrollY();

                if(scrollY > 0) {
                    swipeRefreshParentLayout.setEnabled(false);
                } else {
                    swipeRefreshParentLayout.setEnabled(true);
                }

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        // Save this activity as the current one for accessing it later (from static context)
        AppData.currentActivity = this;

        // Show current user balance
        updateBalanceLayout();

        // Start periodically updating all data in the background
        UpdateDataHandler.startUpdatingData();
    }

    // Set options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }

    // Handle options menu action (there are two of them: options and profile)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout: // Log out user
                FirebaseHandler.logoutUser();
                // Close this activity (open login/main activity)
                HomeActivity.this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // Update list view with all user plates
    static void updatePlatesList() {
        try {
            ((BaseAdapter) plateAdapter).notifyDataSetChanged();

            // Tries to set constant height of the list view
            setListViewHeightBasedOnChildren((ListView) AppData.currentActivity.findViewById(R.id.user_plates_list_view));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Update balance text view to show current user account balance
    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    void updateBalanceLayout() {
        try {
            TextView userBalanceTV = findViewById(R.id.user_balance_text_view);
            userBalanceTV.setText(String.format("%.2f", AppData.userBalance) + " " + getResources().getString(R.string.currency_mark));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Refresh all data and layout with it
    void refreshWholeLayout() {
        // If there is no internet connection, show warning
        checkNetworkConnection();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Get all data
                    JsonHandler.getAllUserPlates();
                    JsonHandler.getUserBalance();

                    // After we got new data, refresh layout
                    HomeActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                updatePlatesList();
                                updateBalanceLayout();

                                // Close loading animation
                                changeRefreshAnimationState(false);
                            } catch (Exception ignored) {}

                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // Sets visibility of refresh/loading animation
    void changeRefreshAnimationState(boolean refresh) {
        // Hide loading animation of SwipeRefreshLayout (I don't have the full control over it, so it it going down :) )
        swipeRefreshParentLayout.setRefreshing(false);

        // Get progress bar layout
        View loadingProgressBar = findViewById(R.id.loading_progress_bar);

        if(!refresh && JsonHandler.waitLoopCount <= 0) {
            // If nothing is downloading/getting form the internet, hide it
            loadingProgressBar.setVisibility(View.GONE);
        } else {
            // Otherwise, show it
            loadingProgressBar.setVisibility(View.VISIBLE);
        }
    }

    // Checks for internet connection and sets warning TextView accordingly
    private void checkNetworkConnection() {
        try {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            final boolean isConnected = activeNetwork != null && activeNetwork.isConnected();

            HomeActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // Set error layout visibility
                        View noInternetConnectionView = findViewById(R.id.no_internet_connection_TV);
                        noInternetConnectionView.setVisibility(isConnected ? View.GONE : View.VISIBLE);
                    } catch (Exception ignored) {}

                }
            });

        } catch (Exception ignored) {}
    }


    /*
     * Method for Setting the Height of the ListView dynamically.
     * Hack to fix the issue of not showing all the items of the ListView
     * when placed inside a ScrollView
     */
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }


    // Open dialog for new plate input
    private void showAddPlateDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners for the positive and negative buttons on the dialog.
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Set view of a dialog with text input
        final View dialogLayout = getLayoutInflater().inflate(R.layout.layout_add_plate_dialog, null, false);
        builder.setView(dialogLayout);

        // Set title of a dialog
        builder.setTitle(R.string.add_plate);

        // Set buttons and their onClick listeners
        builder.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                // Get table from EditText and trim it (remove spaces at the begging and at the end)
                final String newPlate = ((EditText) dialogLayout.findViewById(R.id.add_plate_text_input_view)).getText().toString().trim();

                // If plate is valid, add it
                if(!newPlate.isEmpty()) {
                    // Show loading animation
                    changeRefreshAnimationState(true);

                    // Add it to list and update ListView (for UI change)
                    AppData.userPlates.add(new Plate(newPlate));
                    updatePlatesList();

                    // Add plate to the cloud and show "Done" Toast afterwards
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JsonHandler.setNewPlateData(newPlate, true);

                                HomeActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Close loading animation
                                        changeRefreshAnimationState(false);
                                        Toast.makeText(HomeActivity.this, R.string.table_saved, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }


                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        builder.setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "close" button, so just dismiss the dialog
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();

        // Dialog can be closed on back button click or on outside click (this is default, maybe can be removed)
        alertDialog.setCancelable(true);
        alertDialog.setCanceledOnTouchOutside(true);

        alertDialog.show();
    }


    // Open dialog for new plate input
    private void showAccountPaymentDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners for the positive and negative buttons on the dialog.
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Set view of a dialog with text input
        final View dialogLayout = getLayoutInflater().inflate(R.layout.layout_payment_dialog, null, false);
        ((EditText) dialogLayout.findViewById(R.id.add_payment_decimal_input_view)).setFilters(new
                InputFilter[] {new DecimalDigitsInputFilter(7,2)});
        builder.setView(dialogLayout);

        // Set title of a dialog
        builder.setTitle(R.string.add_to_the_account);

        // Set buttons and their onClick listeners
        builder.setPositiveButton(R.string.add_to_the_account, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                try {
                    // Try to get value from EditText. If exception is thrown, adding to the account won't continue
                    final float valueToAdd = Float.valueOf(((EditText) dialogLayout.findViewById(R.id.add_payment_decimal_input_view)).getText().toString());

                    // If value is valid, add it to the current one
                    if(valueToAdd > 0) {
                        AppData.userBalance += valueToAdd;

                        // Show loading animation
                        changeRefreshAnimationState(true);

                        // Update it in the cloud and show "successful payment" toast
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    JsonHandler.setUserBalance(AppData.userBalance);

                                    HomeActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            // Update layout
                                            updateBalanceLayout();
                                            // Close loading animation
                                            changeRefreshAnimationState(false);
                                            Toast.makeText(HomeActivity.this, R.string.payment_successful, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                } catch (Exception e) {
                    // Show "unsuccessful payment" toast
                    Toast.makeText(HomeActivity.this, R.string.payment_unsuccessful, Toast.LENGTH_SHORT).show();
                }



                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        builder.setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "close" button, so just dismiss the dialog
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();

        // Dialog can be closed on back button click or on outside click (this is default, maybe can be removed)
        alertDialog.setCancelable(true);
        alertDialog.setCanceledOnTouchOutside(true);

        alertDialog.show();
    }

}
