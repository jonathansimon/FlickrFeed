package com.jonathansimon.flickrfeed.messaging;

import android.os.Handler;
import android.os.Looper;

import com.jonathansimon.flickrfeed.FlickrPhotoListActivity;
import com.squareup.otto.Bus;

import static android.R.attr.data;

/**
 * AS with the API manager, and DB Persistence Manager, this class is responsible for managing the
 * Otto bus, for posting and receiving messages throughout the app. It does no processing of the
 * messages.
 */

public class BusManager {
    private static BusManager _instance;

    private Bus bus;
    private BusManager(){
        //One of the gotchas with Otto is that all the messaging to the UI must occur on the
        // UI thread. Since some of the messages are originating in background services, we use a
        // custom UI only bus subclass, that internally handles the threading to add the message to
        // the bus from the UI/main thread.
        bus = new MainThreadOnlyBus();
    }

    public static synchronized BusManager getInstance() {
        if (_instance == null) {
            _instance = new BusManager();
        }
        return _instance;
    }

    public void register(Object object) {
        bus.register(object);
    }

    public void unregister(Object object) {
        bus.unregister(object);
    }

    public void post(Object event) {
        bus.post(event);
    }


}
