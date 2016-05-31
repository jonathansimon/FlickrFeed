package com.jonathansimon.flickrfeedcontentprovidertest;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.CursorLoader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import static com.jonathansimon.flickrfeedcontentprovidertest.R.color.colorPrimaryDark;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private CursorAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshValuesFromContentProvider();

                Snackbar.make(view, "Refreshed Favorites", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        listView = (ListView) findViewById(R.id.list_view);

        adapter = new FavoriteCursorAdapter(getBaseContext(), null, 0);

        listView.setAdapter(adapter);
        refreshValuesFromContentProvider();
    }

    private void refreshValuesFromContentProvider() {
        String URL = "content://com.jonathansimon.flickrfeed.favorites";
        CursorLoader cursorLoader = new CursorLoader(getBaseContext(), Uri.parse(URL),
                null, null, null, null);
        Cursor c = cursorLoader.loadInBackground();
        adapter.swapCursor(c);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

//    public void showFavorites(View view) {
//        // Show all the birthdays sorted by friend's name
//        String URL = "content://com.jonathansimon.flickrfeed.favorites";
//        Uri favorites = Uri.parse(URL);
//        Cursor c = getContentResolver().query(favorites, null, null, null, null);
//        String result = "Results:";
//
//        if (!c.moveToFirst()) {
//            Toast.makeText(this, result+" no content yet!", Toast.LENGTH_LONG).show();
//        }else{
//            do{
//                result = result + "\n" + c.getString(0);
//            } while (c.moveToNext());
//            Toast.makeText(this, result, Toast.LENGTH_LONG).show();
//        }
//
//    }

    class FavoriteCursorAdapter extends CursorAdapter {
        public FavoriteCursorAdapter(Context context, Cursor cursor, int flags) {
            super(context, cursor, 0);
        }

        // The newView method is used to inflate a new view and return it,
        // you don't bind any data to the view at this point.
        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return LayoutInflater.from(context).inflate(R.layout.list_content, parent, false);
        }

        // The bindView method is used to bind all data to a given view
        // such as setting the text on a TextView.
        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            TextView imageView = (TextView)view.findViewById(R.id.image);
            String imageUrl = cursor.getString(4);
            imageView.setText(imageUrl);
            //Picasso.with(MainActivity.this).load(imageUrl).into(imageView);
        }
    }

}
