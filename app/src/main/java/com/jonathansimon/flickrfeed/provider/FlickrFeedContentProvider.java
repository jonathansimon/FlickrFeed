package com.jonathansimon.flickrfeed.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;

import com.jonathansimon.flickrfeed.data.DataPersistenceManager;

/**
 * Created by jonathansimon on 5/30/16.
 */

public class FlickrFeedContentProvider extends ContentProvider {
    
    @Override
    public boolean onCreate() {
        DataPersistenceManager.getInstance().initialize(this.getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return DataPersistenceManager.getInstance().getFavoriteFlickrPhotosCursor();
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return "vnd.android.cursor.dir/vnd.com.jonathansimon.flickrfeed.provider.favorites";
    }

    // The following methods are not implemented as out of scope.

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
