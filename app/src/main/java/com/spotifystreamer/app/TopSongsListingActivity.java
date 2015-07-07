package com.spotifystreamer.app;

import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


public class TopSongsListingActivity extends ActionBarActivity {
    public static boolean syncResult;
    public static final String SYNC_RESULT_URI = "content://spotifystreamer.com/sync_result";
    private Toast toast = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_songs_listing);

        ContentObserver mObserver = new ContentObserver(new Handler()) {
            public void onChange(boolean selfChange) {

                if (!syncResult) {
                    toast = Toast.makeText(getApplicationContext(), getString(R.string.no_songs_found_message), Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else {
                    if (toast != null) {
                        toast.cancel();
                        toast = null;
                    }
                }
            }
        };
        getContentResolver().registerContentObserver(Uri.parse(SYNC_RESULT_URI), false, mObserver);

        if (savedInstanceState == null) {

            Bundle arguments = new Bundle();
            String[] arg = getIntent().getStringArrayExtra(Intent.EXTRA_TEXT);
            arguments.putString(MainActivityFragment.ARTIST_ID_EXTRA_STRING, arg[0]);
            arguments.putString(MainActivityFragment.ARTIST_NAME_EXTRA_STRING, arg[1]);

            TopSongsListingActivityFragment fragment = new TopSongsListingActivityFragment();
            fragment.setTwoPane(false);
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container_songs, fragment)
                    .commit();

        }
//        TopSongsSyncAdapter.initializeSyncAdapter(this);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_top_songs_listing, menu);
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


}
