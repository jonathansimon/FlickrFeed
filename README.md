# FlickrFeed

This Android application implements the requirements for the coding challenge. The requirements are as follows: 

```
We would like you to build a small web-based Flickr client using the public Flickr JSON feed: https://api.flickr.com/services/feeds/photos_public.gne?format=json

The client should:

- Load and display pictures and metadata about each picture, scrollable or page-able in some form
- Update the client with additional de-duplicated content by accessing the feed again every minute
- Allow marking of favorite photos and some sort of mode of the client that displays favorited photos
- Allows for un-favoriting of photos
Bonus points:  Allow accessing your favorites list from other clients via a method of your choice.
```

# Installation Instructions 

You're welcome to clone the git repo, install the android tools and run the project on your own emulator and devices. However, I also uploaded prebuilt APKs of both the main app and the content provider test (for the Bonus points section). The links are as follows: 

 * Main app: <a href="http://jonathansimon.com/flickrfeed_release/flickrfeed-release.apk">http://jonathansimon.com/flickrfeed_release/flickrfeed-release.apk</a>
 * Content Provider Test (intra app favorite sharing): <a href="http://jonathansimon.com/flickrfeed_release/flickrfeed-cp-test-release.apk">http://jonathansimon.com/flickrfeed_cp-test-release/flickrfeed-release.apk</a>

# Usage Instructions 

When you load the application, you will see a list of flickr photos from the feed (photo only in the list). Once a minute, the list will be refreshed with the latest from the feed, de-duplicated (all of the previous photos stay in the list). You will be notified of the update since it's a bit jarring from a user perspective when the list is updated. There are ways to make this cleaner with more time/effort. 

If you click on a photo, you will be taken to a detail view showing the photo with metadata scrolling below the photo. If you press the heart-shaped Floating Action Button (FAB), you will add it to favorites and the heart will be filled in. Press it again and it will be removed from favorites. 

While on the list, you can press the heart-shaped FAB and you will be taken to the Favorites list. From the favorites list, you can view details of the photo (as from the all photoes list). From there you can view details of the photo, and remove it (and re-add it) to the favorites list. 

Clicking on the photo in the details screen will open Flickr link in your Android browser. 

# Architecture Overview

The app is built with Android Studio and Gradle, all IDE files are in the project. You should be able to download Android Studio and clone this repo, then build and run the project. Reach out to me if there are any issues, and I'll help get you set up. 

The app uses a few external libraries: 
 * <a href="https://github.com/google/gson">GSON</a>: This is a library from Google that assists in parsing JSON feeds into Java objects. 
 * <a href="http://square.github.io/otto/">Otto</a>: This is a library from Square that implements a message bus using annotations. It's a dead simple, rock solid, easily to implement bus. This is heavily used in the app to manage asyncronous programming (i.e. post to the bus in one class/thread deep in a service and update the UI at some point later). 
 * <a href="http://square.github.io/Picasso/">Picasso</a>: This is a library from Square that assists in image loading. The main reason this is used is to load an image into an ImageView from a URL without writing all of the connection logic in the app. 
 * <a href="http://square.github.io/okhttp/">OK Http</a>: Great library for http requests. More from Square, they make good libraries. (Ask me why!)
 
From an architectural perspective, the root of the app is the custom <a href="https://github.com/jonathansimon/FlickrFeed/blob/master/app/src/main/java/com/jonathansimon/flickrfeed/FlickrFeedApplication.java>FlickrFeedApplication</a>. This instantiates the database and kicks off the feed timer thread to update the feed every minute. 

Then there are three main managers: 
 * <a href="https://github.com/jonathansimon/FlickrFeed/blob/master/app/src/main/java/com/jonathansimon/flickrfeed/api/ApiClient.java"> APIClient</a>: Encapsulates all of the logic to make the API calls
 * <a href="https://github.com/jonathansimon/FlickrFeed/blob/master/app/src/main/java/com/jonathansimon/flickrfeed/data/DataPersistenceManager.java">DataPersistenceManager</a>: Encapsulates the sqlite DB, and all access to it including inserts, updates, and queries (i.e. get favorite/all flickr feed items, check for duplicates, etc). 
 * <a href="https://github.com/jonathansimon/FlickrFeed/blob/master/app/src/main/java/com/jonathansimon/flickrfeed/messaging/BusManager.java">BusManager</a>: This encapsulates the Otto bus, as the central location to access, post to the bus, and register/unregister from the bus. 
 
There are two screens: 
 * The <a href="https://github.com/jonathansimon/FlickrFeed/blob/master/app/src/main/java/com/jonathansimon/flickrfeed/FlickrPhotoListActivity.java">FlickrPhotoListActivity</a>: This is the main list screen. It handles both the all flicker feed items list, and the favorites list. 
 * The <a href="https://github.com/jonathansimon/FlickrFeed/blob/master/app/src/main/java/com/jonathansimon/flickrfeed/FlickrPhotoDetailFragment.java">FlickrPhotoDetailFragment</a>: This handles the detail view for the photo. This is done by convention as a fragment rather than an activity (as the photo list is) to support two pane views on tablets. However, tablet support is out of scope for this app. 
 
# Bonus
How can you say no to bonus points! The app has a content provider included which is a mechanism for Android apps to share information with other Android apps. You can find the content provider <a href="https://github.com/jonathansimon/FlickrFeed/blob/master/app/src/main/java/com/jonathansimon/flickrfeed/provider/FlickrFeedContentProvider.java">here</a>. There is also a <a href="https://github.com/jonathansimon/FlickrFeed/tree/master/content_provider_test/FlickrFeedContentProviderTest">simple example app</a> that invokes the content provider and displays the resulting image URLs in a list. Note, they are entirely separate apps with separate namespaces. 

When you load the Content Provider Test app, it will automatically ping your locally installed FlickFeed app and pick up your favorites. If you go back to the FlickrFeed app and add a new favorites, you can press the refresh FAB button on the content provider app. Note: Both apps must be installed on the same device. All information between apps is local, no cloud storage of any kind and no accounts/users/passwords - just plain app-to-app sharing. 

# Disclosures / Room for Improvement
There is a limit in what you can do in a short period of time. Given that, I wanted to make it clear a list of shortcomings in the app that would be improved given more time. 

 * DB loading / Caching: Right now, all DB queries from the list activity are done on the main thread, and all query the DB even though the changes are potentialy infrequent compared to the navigation speed. This would be greatly improved by caching the set of photos for the list, and avoiding requeries... and especially not querying on the main thread! 
 * All entries are added to the DB from the feed: Over time, the DB would get potentially huge! Could look into expiring non-favorited old photos. 
 * All entries are queried from the DB every time: Some care should be given to paging the list in case the DB gets really huge. 
 * Favorite list order: This should likely be reverse order from when favorited, but right now it shows the favorites in reverse ID order (last added to DB first) but you may have favorited them out of that order. 
 * Progress indicators: There aren't many. It's good to be overly clear on what's hapening with the app. 
 * Managers -> Services: Some managers (i.e. the FeedManager) would be better immplemented as a service (much higher cost to implement, much more resilient). Having continuous threads running in objects around the app is not ideal. 
 * Data management threading: Better handling of threading and responses around accessing and writing of data to the DB. For example, if you press and unpress the favorite FAB repeatedly, bad things will happen. I've solved this repeatedly before, but it requires more plumbing (i.e. first class 'favorite' objects with statuses, asynchronous calls to the DB layer, UI blocking, bus events, etc). 
 


 


