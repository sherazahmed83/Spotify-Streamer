package com.spotifystreamer.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;
import com.spotifystreamer.app.adapter.CustomArtistListAdapter;
import com.spotifystreamer.app.adapter.Utils;

public class MainActivity extends ActionBarActivity  implements
        PlayerNotificationCallback, ConnectionStateCallback {
    
    private String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String CLIENT_ID = "f90e2f71585d433f93ae23f442ab1f5e";
    private static final String REDIRECT_URI = "spotifystreamerapp://callback";

    // Request code that will be used to verify if the result comes from correct activity
    // Can be any integer
    private static final int REQUEST_CODE = 1337;

    private static String SPOTIFY_ACCESS_TOKEN = "";

    private CustomArtistListAdapter adapter;
    // Search EditText
    private EditText inputSearch;

    private ListView listView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new MainActivityFragment())
                    .commit();
        }

        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID,
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

    @Override
    public void onLoggedIn() {
        Log.d(LOG_TAG, "User logged in");
    }

    @Override
    public void onLoggedOut() {
        Log.d(LOG_TAG, "User logged out");
    }

    @Override
    public void onLoginFailed(Throwable error) {
        Log.d(LOG_TAG, "Login failed");
    }

    @Override
    public void onTemporaryError() {
        Log.d(LOG_TAG, "Temporary error occurred");
    }

    @Override
    public void onConnectionMessage(String message) {
        Log.d(LOG_TAG, "Received connection message: " + message);
    }

    @Override
    public void onPlaybackEvent(EventType eventType, PlayerState playerState) {
        Log.d(LOG_TAG, "Playback event received: " + eventType.name());
    }

    @Override
    public void onPlaybackError(ErrorType errorType, String errorDetails) {
        Log.d(LOG_TAG, "Playback error received: " + errorType.name());
    }

    @Override
    protected void onDestroy() {
       //AuthenticationClient.logout(this);
        super.onDestroy();
    }

}
