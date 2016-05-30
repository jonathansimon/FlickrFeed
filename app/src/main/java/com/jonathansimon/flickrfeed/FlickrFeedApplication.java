package com.jonathansimon.flickrfeed;

import android.app.Application;

import com.jonathansimon.flickrfeed.data.DataPersistenceManager;

/**
 * Application class that initializes resources and services on app launch.
 */

public class FlickrFeedApplication extends Application {

    @Override
    public void onCreate() {
        // Instantiate the DB manager with needed context.
        DataPersistenceManager.getInstance().initialize(this);

        // Start the feed manager, which checks the feed every minute and updates the UI.
        FeedManager.getInstance().startFeeding();
    }



}
