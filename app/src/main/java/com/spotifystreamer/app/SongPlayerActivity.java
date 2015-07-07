package com.spotifystreamer.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;

/**
 * 1- How to put android library images on ImageButtons
 * http://tagasks.com/change_color_of_an_image_in_android
 */

public class SongPlayerActivity extends FragmentActivity {

    public static final String SONG_PLAYER_DIALOG_TAG = "song_player_dialog_tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_player);

        Intent intent = getIntent();
        if (intent != null) {
            SongPlayerActivityFragment fragment = new SongPlayerActivityFragment();

            Bundle arguments = new Bundle();
            arguments.putStringArray(SongPlayerActivityFragment.TRACK_META_DATA, intent.getStringArrayExtra(Intent.EXTRA_TEXT));
            fragment.setArguments(arguments);

            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.song_player_container, fragment)
                        .commit();
            }

        }

    }// end of onCreate()


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_song_player, menu);
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
    }

}
