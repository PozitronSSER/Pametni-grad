package com.sser.smartcity.smartcitypay;


// Interface for constantly getting/updating data from the internet
class UpdateDataHandler {

    // Thread on witch data is periodically refreshing
    private static Thread updateThread = null;

    // Start periodically checking for new data (running in the background)
    public static void startUpdatingData() {
        // If thread is already running, stop id
        try {
            updateThread.interrupt();
            updateThread = null;
        } catch (Exception ignored) {}

        // Start checking and assign this thread to the updateThread
        updateThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while(true) {
                        // Try to update all data and it's layout
                        try {
                            ((HomeActivity) AppData.currentActivity).refreshWholeLayout();
                        } catch (Exception ignored) {}

                        try {
                            Thread.sleep(5000);
                        } catch (Exception e) {
                            // Exception is thrown when thread is interrupted, in that case, exit infinity loop
                            return;
                        }
                    }
                } catch (Exception ignored) {}
            }
        });
        updateThread.start();
    }

}
