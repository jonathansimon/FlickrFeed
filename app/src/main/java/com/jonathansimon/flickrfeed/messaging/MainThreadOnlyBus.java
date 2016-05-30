package com.jonathansimon.flickrfeed.messaging;

import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;

/**
 * Created by jonathansimon on 5/27/16.
 */

public class MainThreadOnlyBus extends Bus {
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    public void post(final Object event) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            super.post(event);
        } else {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    MainThreadOnlyBus.super.post(event);
                }
            });
        }
    }
}