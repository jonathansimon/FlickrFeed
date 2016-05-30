package com.jonathansimon.flickrfeed.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * This helper class is used to open the DB, keep track of column names, create the initial
 * table(s), and so on. The convention is to keep DB information (table & column names, etc) in
 * constants to make DB access code easier to understand, refactor, etc.
 */

public class FlickrFeedDbHelper extends SQLiteOpenHelper {

    public static final String FLICKR_PHOTO_TABLE_NAME = "flickr_photos";

    public static final String FLICKR_PHOTO_ID = " _id";
    public static final String FLICKR_PHOTO_TITLE = " title";
    public static final String FLICKR_PHOTO_LINK = " link";
    public static final String FLICKR_PHOTO_MEDIA_M = " media_m";
    public static final String FLICKR_PHOTO_DATE_TAKEN = " date_taken";
    public static final String FLICKR_PHOTO_DESCRIPTION = " description";
    public static final String FLICKR_PHOTO_PUBLISHED = " published";
    public static final String FLICKR_PHOTO_AUTHOR = " author";
    public static final String FLICKR_PHOTO_AUTHOR_ID = " author_id";
    public static final String FLICKR_PHOTO_TAGS = " tags";
    public static final String FLICKR_PHOTO_IS_FAVORITE = " is_favorite";


    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + FLICKR_PHOTO_TABLE_NAME + " (" +
                    FLICKR_PHOTO_ID + " INTEGER PRIMARY KEY,"  +
                    FLICKR_PHOTO_TITLE + TEXT_TYPE + COMMA_SEP +
                    FLICKR_PHOTO_LINK + TEXT_TYPE + COMMA_SEP +
                    FLICKR_PHOTO_MEDIA_M + TEXT_TYPE + COMMA_SEP +
                    FLICKR_PHOTO_DATE_TAKEN + TEXT_TYPE + COMMA_SEP +
                    FLICKR_PHOTO_DESCRIPTION + TEXT_TYPE + COMMA_SEP +
                    FLICKR_PHOTO_PUBLISHED + TEXT_TYPE + COMMA_SEP +
                    FLICKR_PHOTO_AUTHOR + TEXT_TYPE + COMMA_SEP +
                    FLICKR_PHOTO_AUTHOR_ID + TEXT_TYPE + COMMA_SEP +
                    FLICKR_PHOTO_TAGS + TEXT_TYPE + COMMA_SEP +
                    FLICKR_PHOTO_IS_FAVORITE + TEXT_TYPE +
                    " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + FLICKR_PHOTO_TABLE_NAME;


    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "FlickrFeed.db";

    public FlickrFeedDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}