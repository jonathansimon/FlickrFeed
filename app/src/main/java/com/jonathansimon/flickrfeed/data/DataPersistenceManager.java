package com.jonathansimon.flickrfeed.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jonathansimon.flickrfeed.api.model.FlickrPhoto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class is the applications interface to persistent data storage. The sqlite DB sits behind
 * here, and this class manages all inserts, updates, and queries.
 */

public class DataPersistenceManager {
    private static DataPersistenceManager _instance;

    private FlickrFeedDbHelper dbHelper;
    SQLiteDatabase db;

    static String[] projection = {
            FlickrFeedDbHelper.FLICKR_PHOTO_ID,
            FlickrFeedDbHelper.FLICKR_PHOTO_TITLE,
            FlickrFeedDbHelper.FLICKR_PHOTO_LINK,
            FlickrFeedDbHelper.FLICKR_PHOTO_DESCRIPTION,
            FlickrFeedDbHelper.FLICKR_PHOTO_MEDIA_M,
            FlickrFeedDbHelper.FLICKR_PHOTO_DATE_TAKEN,
            FlickrFeedDbHelper.FLICKR_PHOTO_DESCRIPTION,
            FlickrFeedDbHelper.FLICKR_PHOTO_PUBLISHED,
            FlickrFeedDbHelper.FLICKR_PHOTO_AUTHOR,
            FlickrFeedDbHelper.FLICKR_PHOTO_AUTHOR_ID,
            FlickrFeedDbHelper.FLICKR_PHOTO_TAGS,
            FlickrFeedDbHelper.FLICKR_PHOTO_IS_FAVORITE
    };

    public static synchronized DataPersistenceManager getInstance() {
        if (_instance == null) {
            _instance = new DataPersistenceManager();
        }
        return _instance;
    }

    private DataPersistenceManager(){

    }

    public void initialize(Context context) {
        dbHelper = new FlickrFeedDbHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    public long addFlickrPhoto(FlickrPhoto photo) {
        ContentValues values = new ContentValues();
        values.put(FlickrFeedDbHelper.FLICKR_PHOTO_TITLE, photo.getTitle());
        values.put(FlickrFeedDbHelper.FLICKR_PHOTO_LINK, photo.getLink());
        values.put(FlickrFeedDbHelper.FLICKR_PHOTO_DESCRIPTION, photo.getDescription());
        values.put(FlickrFeedDbHelper.FLICKR_PHOTO_MEDIA_M, photo.getMedia().get("m"));
        values.put(FlickrFeedDbHelper.FLICKR_PHOTO_DATE_TAKEN, photo.getDateTaken());
        values.put(FlickrFeedDbHelper.FLICKR_PHOTO_DESCRIPTION, photo.getDescription());
        values.put(FlickrFeedDbHelper.FLICKR_PHOTO_PUBLISHED, photo.getPublished());
        values.put(FlickrFeedDbHelper.FLICKR_PHOTO_AUTHOR, photo.getAuthor());
        values.put(FlickrFeedDbHelper.FLICKR_PHOTO_AUTHOR_ID, photo.getAuthorId());
        values.put(FlickrFeedDbHelper.FLICKR_PHOTO_TAGS, photo.getTags());
        values.put(FlickrFeedDbHelper.FLICKR_PHOTO_IS_FAVORITE, false);

        // Insert the new row, returning the primary key value of the new row
        long newRowId;
        newRowId = db.insert(
                FlickrFeedDbHelper.FLICKR_PHOTO_TABLE_NAME,
                "null",
                values);

        return newRowId;
    }

    public boolean isDuplicate(FlickrPhoto photo) {
        Cursor c = db.query(
                FlickrFeedDbHelper.FLICKR_PHOTO_TABLE_NAME,
                new String[]{ FlickrFeedDbHelper.FLICKR_PHOTO_ID},
                FlickrFeedDbHelper.FLICKR_PHOTO_MEDIA_M + " = ?",
                new String[]{photo.getMedia().get("m")},
                null,
                null,
                null
        );

        if (c.moveToFirst()) {
            return true;
        } else {
            return false;
        }
    }

    public List<FlickrPhoto> getAllFlickrPhotos() {
        String sortOrder =
                FlickrFeedDbHelper.FLICKR_PHOTO_ID + " DESC";

        Cursor c = db.query(
                FlickrFeedDbHelper.FLICKR_PHOTO_TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        );

        return convertCursorToFlickrPhotos(c);
    }

    public List<FlickrPhoto> getFavoriteFlickrPhotos() {
        Cursor c = getFavoriteFlickrPhotosCursor();
        return convertCursorToFlickrPhotos(c);
    }

    public Cursor getFavoriteFlickrPhotosCursor() {
        String sortOrder =
                FlickrFeedDbHelper.FLICKR_PHOTO_ID + " DESC";

        Cursor c = db.query(
                FlickrFeedDbHelper.FLICKR_PHOTO_TABLE_NAME,
                projection,
                FlickrFeedDbHelper.FLICKR_PHOTO_IS_FAVORITE + " = ?",
                new String[]{ "1" },
                null,
                null,
                sortOrder
        );

        return c;
    }

    public FlickrPhoto getFlickrPhoto(long id) {
        String sortOrder =
                FlickrFeedDbHelper.FLICKR_PHOTO_ID + " DESC";

        Cursor c = db.query(
                FlickrFeedDbHelper.FLICKR_PHOTO_TABLE_NAME,
                projection,
                FlickrFeedDbHelper.FLICKR_PHOTO_ID + " = ?",
                new String[]{"" + id},
                null,
                null,
                sortOrder
        );

        return convertCursorToFlickrPhotos(c).get(0);
    }

    private ArrayList<FlickrPhoto> convertCursorToFlickrPhotos(Cursor c) {
        ArrayList<FlickrPhoto> photosToReturn = new ArrayList<FlickrPhoto>();

        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                FlickrPhoto photo = new FlickrPhoto();
                photo.setLocalId(c.getLong(0));
                photo.setTitle(c.getString(1));
                photo.setLink(c.getString(2));
                photo.setDescription(c.getString(3));

                HashMap<String, String> media = new HashMap<String, String>();
                media.put("m", c.getString(4));
                photo.setMedia(media);

                photo.setDateTaken(c.getString(5));
                photo.setDescription(c.getString(6));
                photo.setPublished(c.getString(7));
                photo.setAuthor(c.getString(8));
                photo.setAuthorId(c.getString(9));
                photo.setTags(c.getString(10));

                photo.setFavorite( (c.getInt(11) == 1) );

                photosToReturn.add(photo);

                c.moveToNext();
            }
        }

        c.close();

        return photosToReturn;
    }

    public FlickrPhoto updateFlickrPhotoFavoriteStatus(FlickrPhoto photo, boolean isFavorite) {
        ContentValues values = new ContentValues();
        values.put(FlickrFeedDbHelper.FLICKR_PHOTO_IS_FAVORITE, isFavorite);

        db.update(
                FlickrFeedDbHelper.FLICKR_PHOTO_TABLE_NAME,
                values,
                FlickrFeedDbHelper.FLICKR_PHOTO_ID + " = ?",
                new String[]{"" + photo.getLocalId()});

        return getFlickrPhoto(photo.getLocalId());
    }
}

