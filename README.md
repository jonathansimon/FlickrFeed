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

# User Instructions 

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
  
From an architectural perspective, the root of the app is the custom <a href="https://github.com/jonathansimon/FlickrFeed/blob/master/app/src/main/java/com/jonathansimon/flickrfeed/FlickrFeedApplication.java>FlickrFeedApplication</a>. This instantiates the database and kicks off the feed timer thread to update the feed every minute. 

Then there are three main managers: 
  * <a href="https://github.com/jonathansimon/FlickrFeed/blob/master/app/src/main/java/com/jonathansimon/flickrfeed/api/ApiClient.java"> APIClient</a>: Encapsulates all of the logic to make the API calls
  * <a href="https://github.com/jonathansimon/FlickrFeed/blob/master/app/src/main/java/com/jonathansimon/flickrfeed/data/DataPersistenceManager.java">DataPersistenceManager</a>: Encapsulates the sqlite DB, and all access to it including inserts, updates, and queries (i.e. get favorite/all flickr feed items, check for duplicates, etc). 
  * <a href="https://github.com/jonathansimon/FlickrFeed/blob/master/app/src/main/java/com/jonathansimon/flickrfeed/messaging/BusManager.java">BusManager</a>: This encapsulates the Otto bus, as the central location to access, post to the bus, and register/unregister from the bus. 
  
There are two screens: 
  * The <a href="https://github.com/jonathansimon/FlickrFeed/blob/master/app/src/main/java/com/jonathansimon/flickrfeed/FlickrPhotoListActivity.java">FlickrPhotoListActivity</a>: This is the main list screen. It handles both the all flicker feed items list, and the favorites list. 
  * The <a href="https://github.com/jonathansimon/FlickrFeed/blob/master/app/src/main/java/com/jonathansimon/flickrfeed/FlickrPhotoDetailFragment.java">FlickrPhotoDetailFragment</a>: This handles the detail view for the photo. 

