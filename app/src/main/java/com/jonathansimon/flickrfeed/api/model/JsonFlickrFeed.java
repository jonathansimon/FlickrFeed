package com.jonathansimon.flickrfeed.api.model;

import com.google.gson.annotations.SerializedName;

import java.util.Set;

/*
 * This is a GSON class that is used to parse the flickr feed. This model is also used throughout
 * the application as the container of FlickrPhotos coming through the feed. (i.e. in the
 * FeedAutoUpdatedEvent).
 */

public class JsonFlickrFeed {

    private String title;
    private String link;
    private String description;
    private String modified;
    private String generator;

    @SerializedName("items")
    private Set<FlickrPhoto> flickrPhotos;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public String getGenerator() {
        return generator;
    }

    public void setGenerator(String generator) {
        this.generator = generator;
    }

    public Set<FlickrPhoto> getFlickrPhotos() {
        return flickrPhotos;
    }

    public void setFlickrPhotos(Set<FlickrPhoto> flickrPhotos) {
        this.flickrPhotos = flickrPhotos;
    }
}
