package com.jonathansimon.flickrfeed;

import android.util.Log;

import com.jonathansimon.flickrfeed.api.ApiClient;
import com.jonathansimon.flickrfeed.api.model.FlickrPhoto;
import com.jonathansimon.flickrfeed.api.model.JsonFlickrFeed;
import com.jonathansimon.flickrfeed.data.DataPersistenceManager;
import com.jonathansimon.flickrfeed.messaging.ApiFeedReturnedEvent;
import com.jonathansimon.flickrfeed.messaging.BusManager;
import com.jonathansimon.flickrfeed.messaging.FeedAutoUpdatedEvent;
import com.squareup.otto.Subscribe;

import java.util.Iterator;

/**
 * This class is responsible for managing the feed and containing business logic surrounding the
 * feed. It relies on other managers (DB, API, etc.) for other processing. This class checks the
 * feed every 60 seconds, and also is responsible for determining duplicates.
 */
public class FeedManager {
    private static FeedManager _instance;

    public static FeedManager getInstance() {
        if (_instance == null) {
            _instance = new FeedManager();
        }

        return _instance;
    }

    private FeedManager() {
        BusManager.getInstance().register(this);
    }

    public void startFeeding() {

        // NOTE: This 1 minute feed check is running in a thread inside this service. To make the
        // app production ready, this would be moved to a first class Android service.
        Thread th = new Thread() {
            public void run() {
                while (true) {
                    updateFeed();
                    try {
                        sleep(60 * 1000);
                    } catch (Exception e) {
                        Log.e("FlickrFeed", "Sleeping failed", e);
                    }
                }
            }
        };
        th.start();
    }

    private void updateFeed() {
        ApiClient.getInstance().getLatestFeed();
    }

    private void addFeedItemsToDataManager(JsonFlickrFeed feed) {
        Iterator<FlickrPhoto> photoIterator = feed.getFlickrPhotos().iterator();
        while (photoIterator.hasNext()) {
            FlickrPhoto photo = photoIterator.next();
            // Check for duplicate photo. If dup, don't add to the DB.
            if (!DataPersistenceManager.getInstance().isDuplicate(photo)) {
                DataPersistenceManager.getInstance().addFlickrPhoto(photo);
                Log.d("FlickrFeed", "Added photo: " + photo.getMedia().get("m"));
            } else {
                Log.d("FlickrFeed", "Duplicate photo: " + photo.getMedia().get("m"));
            }
        }
    }

    @Subscribe
    public void onApiFeedReturned(ApiFeedReturnedEvent event) {
        // This method is an Otto bus listener method that receives a message from the bus when
        // the API call to retrieve the flickr photos is returned.
        addFeedItemsToDataManager(event.getFeed());
        BusManager.getInstance().post(new FeedAutoUpdatedEvent());
    }



}
