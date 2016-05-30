package com.jonathansimon.flickrfeed.api;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jonathansimon.flickrfeed.api.model.JsonFlickrFeed;
import com.jonathansimon.flickrfeed.messaging.ApiFeedReturnedEvent;
import com.jonathansimon.flickrfeed.messaging.BusManager;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * This is the class that is responsible for all API communication. It does not do any processing.
 * Rather, it makes the API call, and parses the response with GSON.
 */

public class ApiClient {
    private static ApiClient _instance;

    private ApiClient(){}

    public static synchronized ApiClient getInstance() {
        if (_instance == null) {
            _instance = new ApiClient();
        }
        return _instance;
    }


    public void getLatestFeed() {

        try {
            Thread th = new Thread() {
                public void run() {

                    OkHttpClient client = new OkHttpClient();

                    Request request = new Request.Builder()
                            .url("https://api.flickr.com/services/feeds/photos_public.gne?format=json")
                            .build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.d("Response Failed", "", e);
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {

                            // This little hack here is because the json flickr feed starts with
                            // "jsonFlickrFeed(" and ends with a ")". In order to make this valid
                            // json and to allow the GSON  parser to work properly, we're stripping
                            // the extra characters out here.
                            String json = response.body().string();
                            json = json.substring(15, json.length()-1);

                            // Instantiate the gson parser, and parse the json
                            // feed into our custom model objects (JsonFlickrFeed, and FlickrPhoto)
                            GsonBuilder gsonBuilder = new GsonBuilder();
                            Gson gson = gsonBuilder.create();
                            JsonFlickrFeed feed = gson.fromJson(json, JsonFlickrFeed.class);

                            // Post the response to the bus
                            ApiFeedReturnedEvent event = new ApiFeedReturnedEvent(feed);
                            BusManager.getInstance().post(event);
                        }
                    });
                }
            };
            th.start();

        } catch (Exception e) {
            //NOTE: All error handling out of scope
            Log.e("FlickrFeed", "OkHttp req failed", e);
        }
    }

}
