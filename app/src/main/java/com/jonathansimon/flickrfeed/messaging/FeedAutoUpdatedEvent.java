package com.jonathansimon.flickrfeed.messaging;

/**
 * There is a background process running that checks the feed every 60 seconds and ads any new
 * photos to the DB. This event is fired when the new items are added so the app display can update
 * itself with the new items.
 */

public class FeedAutoUpdatedEvent {
}
