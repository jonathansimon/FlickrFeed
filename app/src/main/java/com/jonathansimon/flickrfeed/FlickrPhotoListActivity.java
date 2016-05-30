package com.jonathansimon.flickrfeed;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.jonathansimon.flickrfeed.api.model.FlickrPhoto;
import com.jonathansimon.flickrfeed.data.DataPersistenceManager;
import com.jonathansimon.flickrfeed.messaging.BusManager;
import com.jonathansimon.flickrfeed.messaging.FeedAutoUpdatedEvent;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static android.support.design.R.styleable.RecyclerView;

/**
 * An activity representing a list of FlickrPhotos.
 */
public class FlickrPhotoListActivity extends AppCompatActivity {

    // Note: This list works in two modes: (1) 'all' which shows everything from the flickr json
    // feed, and (2) 'favorites' which shows only the photos that the use has favorited. This
    // complexity is much simpler/better than duplicating the entire list/detail structure +
    // managing the navigation since they are nearly identical with the exception of the
    // source of the data. The other nice thing about this is that the detail view doesn't need
    // access to, or is any way effected by the mode - viewing the detail of a photo from the
    // favorites or the main list is the same. Not splitting out the list activity keeps navigation
    // MUCH easier.

    private static final String LIST_STATE_ALL = "all";
    private static final String LIST_STATE_FAVORITES = "favorites";
    private String listState = LIST_STATE_ALL;
    private String KEY_LIST_STATE = "key_list_state";

    SharedPreferences sharedPreferences;

    private SimpleItemRecyclerViewAdapter listAdapter;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flickrphoto_list);

        sharedPreferences = getSharedPreferences("ViewPreferences", Context.MODE_PRIVATE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchListState();
            }
        });

        // The recycler view is the list view in newer Android
        recyclerView = (RecyclerView) findViewById(R.id.flickrphoto_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        // We're storing the list state in shared preferences. This is done so that when you
        // navigate to/from the photo details screen, you come back to the list view in the
        // correct list state.
        String saved_list_state = sharedPreferences.getString(KEY_LIST_STATE, "");
        if (!saved_list_state.isEmpty()) {
            //We have a previously saved list state
            listState = saved_list_state;
        } else {
            //Store it for the first load
            SharedPreferences.Editor ed = sharedPreferences.edit();
            ed.putString(KEY_LIST_STATE, listState);
            ed.commit();
        }

        configureForListState();
    }

    private void configureForListState() {
        // This method is the single location to update the diaplay based on the list state.
        // This includes updating the FAB icon, and the actual list contents.

        if (listState.equals(LIST_STATE_ALL)) {
            fab.setImageResource(R.mipmap.ic_favorite_white_48dp);
            listAdapter.setItems(DataPersistenceManager.getInstance().getAllFlickrPhotos());
        } else {
            fab.setImageResource(R.mipmap.ic_list_white_48dp);
            listAdapter.setItems(DataPersistenceManager.getInstance().getFavoriteFlickrPhotos());
        }
    }

    private void switchListState() {
        // This method changes the list state. It simply sets it to the other state. Then updates
        // stored value in shared preferences, and calls configure to update the display.

        if (listState.equals(LIST_STATE_ALL)) {
            listState = LIST_STATE_FAVORITES;
        } else {
            listState = LIST_STATE_ALL;
        }

        //Store in preferences
        SharedPreferences.Editor ed = sharedPreferences.edit();
        ed.putString(KEY_LIST_STATE, listState);
        ed.commit();

        configureForListState();
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        listAdapter = new SimpleItemRecyclerViewAdapter(new ArrayList<FlickrPhoto>());
        recyclerView.setAdapter(listAdapter);
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {
        // This is a boilerplate list/recycler view adapter to manage the displayed list of
        // flickr photoes and configure the list items to display the selected content.


        private final List<FlickrPhoto> mValues;

        public SimpleItemRecyclerViewAdapter(List<FlickrPhoto> items) {
            mValues = items;
        }

        public void setItems(List<FlickrPhoto> items) {
            mValues.clear();
            mValues.addAll(items);
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.flickrphoto_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            // This is where the list item is updated to display the content.

            holder.mPhoto = mValues.get(position);

            Picasso.with(FlickrPhotoListActivity.this).load(holder.mPhoto.getLargePhotoUrl()).into(holder.mImageView);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, FlickrPhotoDetailActivity.class);
                    intent.putExtra(FlickrPhotoDetailFragment.ARG_ITEM_ID, "" + holder.mPhoto.getLocalId());

                    context.startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final ImageView mImageView;
            public FlickrPhoto mPhoto;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mImageView = (ImageView) view.findViewById(R.id.image);
            }
        }
    }

    //Lifecycle Events

    @Override
    public void onStart() {
        super.onStart();
        BusManager.getInstance().register(this);
    }

    @Override
    public void onStop() {
        super.onStart();
        BusManager.getInstance().unregister(this);
    }

    //Bus methods

    @Subscribe public void feedUpdated(FeedAutoUpdatedEvent event) {
        // Don't update the list if in favorites mode. This would wipe out the favorites display
        // since we're replacing the contents of the list with the latest when the feed is updated.
        // When we switch list modes, we requery the DB, so no need to update in the background.
        if (listState.equals(LIST_STATE_FAVORITES)) {
            return;
        }

        // When the feed is updated, just query the DB for all photos and update the list. The
        // sort order from the DB is reverse ID order, so you'll always see the lastest ones first.
        // To scale this for a real prod app, we'd want to do paging, get this off the UI thread,
        // etc. etc.
        listAdapter.setItems(DataPersistenceManager.getInstance().getAllFlickrPhotos());

        // Lightweight notification to the user that the feed is updating
        Snackbar.make(recyclerView, "Feed updated.", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
}
