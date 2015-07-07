package com.spotifystreamer.app;

import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotifystreamer.app.adapter.CustomArtistListAdapter;
import com.spotifystreamer.app.adapter.Utils;
import com.spotifystreamer.app.service.TopSongsSyncAdapter;

public class MainActivity extends ActionBarActivity implements MainActivityFragment.Callback/*  implements
        PlayerNotificationCallback, ConnectionStateCallback */{
    
    private String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String REDIRECT_URI = "spotifystreamerapp://callback";

    // Request code that will be used to verify if the result comes from correct activity
    // Can be any integer
    private static final int REQUEST_CODE = 1337;
    private static final String TOPSONGS_LISTING_ACTIVITYFRAGMENT_TAG = "TSLAFTAG";
    private static String SPOTIFY_ACCESS_TOKEN = "";

    private CustomArtistListAdapter adapter;
    // Search EditText
    private EditText inputSearch;
    private ListView listView;

    private boolean mTwoPane;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.container_songs) != null) {
            mTwoPane = true;

            TopSongsListingActivityFragment fragment = new TopSongsListingActivityFragment();
            fragment.setTwoPane(mTwoPane);
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container_songs, fragment, TOPSONGS_LISTING_ACTIVITYFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
            getSupportActionBar().setElevation(0f);
        }


        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(Utils.CLIENT_ID,
                AuthenticationResponse.Type.TOKEN,
                REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "streaming", "playlist-read-private", "playlist-modify-private", "user-library-read", "playlist-read-collaborative"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            if (response.getType() == AuthenticationResponse.Type.TOKEN) {
                Utils.SPOTIFY_ACCESS_TOKEN = response.getAccessToken();
                Log.v(LOG_TAG, "The AccessToken recieved: " + response.getAccessToken());

            }
        }
    }
    public void onItemSelected(String artistId, String artistName){
        Bundle bundle = new Bundle();
        bundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_MANUAL, true);
        bundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putString(MainActivityFragment.ARTIST_ID_EXTRA_STRING, artistId);
        bundle.putString(MainActivityFragment.ARTIST_NAME_EXTRA_STRING, artistName);

        ContentResolver.requestSync(TopSongsSyncAdapter.getSyncAccount(this), getString(R.string.content_authority), bundle);

        if (mTwoPane) {
            TopSongsListingActivityFragment sf = new TopSongsListingActivityFragment();
            sf.setTwoPane(mTwoPane);
            sf.setArguments(bundle);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            transaction.replace(R.id.container_songs, sf, TOPSONGS_LISTING_ACTIVITYFRAGMENT_TAG);
            transaction.addToBackStack(null);

            // Commit the transaction
            transaction.commit();

        } else {
            Intent intent = new Intent(this, TopSongsListingActivity.class);
            intent.putExtra(Intent.EXTRA_TEXT, new String[] {artistId, artistName});
            startActivity(intent);
        }
    }


}
