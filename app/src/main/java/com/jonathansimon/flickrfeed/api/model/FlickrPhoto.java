package com.jonathansimon.flickrfeed.api.model;

import com.google.gson.annotations.SerializedName;

import java.util.HashSet;
import java.util.Map;


/*
 * This is a GSON class that is used to parse feed items from the flickr feed. (Note these are called "items" in the feed,
 * but I chose to call the model class FlickrPhoto instead.) This model is used throughout the codebase as the main model
 * associated with a flickr photo data object.
 */


public class FlickrPhoto {

    private long localId;

    private String title;

    private String link;

    private Map<String, String> media;

    @SerializedName("date_taken")
    private String dateTaken;

    private String description;

    private String published;

    private String author;

    @SerializedName("author_id")
    private String authorId;

    private String tags;

    //used locally only
    private boolean isFavorite;


    public long getLocalId() {
        return localId;
    }

    public void setLocalId(long localId) {
        this.localId = localId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Map<String, String> getMedia() {
        return media;
    }

    public void setMedia(Map<String, String> media) {
        this.media = media;
    }

    public String getDateTaken() {
        return dateTaken;
    }

    public void setDateTaken(String dateTaken) {
        this.dateTaken = dateTaken;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPublished() {
        return published;
    }

    public void setPublished(String published) {
        this.published = published;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    // Helper methods

    public String getLargePhotoUrl() {
        // The flickr feed passes a smallish photo (240x180). This updates the photo URL to be
        // flickr's large photo size (1024x768). It looks MUCH better on device.

        String photoUrl = getMedia().values().iterator().next();
        if (photoUrl == null || photoUrl.isEmpty()) {
            return photoUrl;
        } else {
            return photoUrl.replace("m.jpg", "b.jpg");
        }
    }
}
