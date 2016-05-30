package com.jonathansimon.flickrfeed;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jonathansimon.flickrfeed.api.model.FlickrPhoto;
import com.jonathansimon.flickrfeed.data.DataPersistenceManager;
import com.squareup.picasso.Picasso;

/**
 * A fragment representing a single FlickrPhoto detail screen.
 * This fragment is either contained in a {@link FlickrPhotoListActivity}
 * in two-pane mode (on tablets) or a {@link FlickrPhotoDetailActivity}
 * on handsets.
 */
public class FlickrPhotoDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private FlickrPhoto mPhoto;

    private FloatingActionButton fab;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FlickrPhotoDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            String itemIdString = getArguments().getString(ARG_ITEM_ID);
            long itemId = Long.valueOf(itemIdString);
            mPhoto = DataPersistenceManager.getInstance().getFlickrPhoto(itemId);

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mPhoto.getTitle());
            }

            fab = (FloatingActionButton) activity.findViewById(R.id.fab);
            updateFabStatus();
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mPhoto = DataPersistenceManager.getInstance().updateFlickrPhotoFavoriteStatus(mPhoto, !mPhoto.isFavorite());
                    updateFabStatus();
                }
            });
        }
    }

    private void updateFabStatus() {
        if (mPhoto.isFavorite()) {
            fab.setImageResource(R.mipmap.ic_favorite_white_36dp);
        } else {
            fab.setImageResource(R.mipmap.ic_favorite_border_white_36dp);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.flickrphoto_detail, container, false);

        if (mPhoto!= null) {
            TextView descriptionView = ((TextView) rootView.findViewById(R.id.flickrphoto_detail));

            StringBuffer description = new StringBuffer();

            description.append("Title: ");
            description.append(mPhoto.getTitle());
            description.append("\n\n");

            description.append("Photo URL: ");
            description.append(mPhoto.getMedia().get("m"));
            description.append("\n\n");

            description.append("Date Taken: ");
            description.append(mPhoto.getDateTaken());
            description.append("\n\n");

            description.append("Date Published: ");
            description.append(mPhoto.getPublished());
            description.append("\n\n");

            description.append("Author: ");
            description.append(mPhoto.getAuthor());
            description.append("\n\n");

            description.append("Author ID: ");
            description.append(mPhoto.getAuthorId());
            description.append("\n\n");

            description.append("Tags: ");
            description.append(mPhoto.getTags());
            description.append("\n\n");

            descriptionView.setText(description);

            ImageView imageView = (ImageView) rootView.findViewById(R.id.flickrphoto_image);
            imageView.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mPhoto.getLink()));
                            startActivity(browserIntent);
                        }
                    }
            );

            Picasso.with(getContext()).load(mPhoto.getLargePhotoUrl()).into(imageView);
        }

        return rootView;
    }
}
