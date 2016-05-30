package com.jonathansimon.flickrfeed.messaging;

import com.jonathansimon.flickrfeed.api.model.JsonFlickrFeed;

/**
 * The event used by the Otto bus when the API returns. This allows async calls to remote resources
 * using the bus rather than intricate threading and blocking.
 */

public class ApiFeedReturnedEvent {
    private JsonFlickrFeed feed;

    public ApiFeedReturnedEvent(JsonFlickrFeed feed) {
        this.feed = feed;
    }

    public JsonFlickrFeed getFeed() {
        return feed;
    }
}
