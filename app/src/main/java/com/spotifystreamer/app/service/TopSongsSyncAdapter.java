package com.spotifystreamer.app.service;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.spotifystreamer.app.MainActivityFragment;
import com.spotifystreamer.app.R;
import com.spotifystreamer.app.TopSongsListingActivity;
import com.spotifystreamer.app.TopSongsListingActivityFragment;
import com.spotifystreamer.app.adapter.Utils;
import com.spotifystreamer.app.data.SpotifyStreamerContract;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;

public class TopSongsSyncAdapter extends AbstractThreadedSyncAdapter {
    public final String LOG_TAG = TopSongsSyncAdapter.class.getSimpleName();

    // Interval at which to sync with the songs, in seconds.
    // 60 seconds (1 minute) * 180 = 3 hours
    public static final int SYNC_INTERVAL = 60 * 180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;



    public TopSongsSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(LOG_TAG, "onPerformSync Called.");

        String artistId = "";
        String artistName = "";

        if (extras != null && extras.containsKey(MainActivityFragment.ARTIST_ID_EXTRA_STRING)) {
            artistId = extras.getString(MainActivityFragment.ARTIST_ID_EXTRA_STRING);
            artistName = extras.getString(MainActivityFragment.ARTIST_NAME_EXTRA_STRING);
        }

        if (artistId == null || artistId.equals("") || artistId.trim().equals("")) return;

        //First check if the database has already the records for Top Songs for the artistName

        Cursor cursor = getContext().getContentResolver().query(SpotifyStreamerContract.TrackEntry.buildTrackUriWithArtistName(artistName), TopSongsListingActivityFragment.TRACK_COLUMNS, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            //Do nothing that means that this artist's top songs are already in database
            // just close the Cursor for stopping memory leak.
            cursor.close();
        } else {
            if (cursor != null) {
                cursor.close();
            }

            try {


                SpotifyApi api = new SpotifyApi();
                api.setAccessToken(Utils.SPOTIFY_ACCESS_TOKEN);
                SpotifyService service = api.getService();
                Map<String, Object> qMap = new HashMap<String, Object>();
                qMap.put(SpotifyService.COUNTRY, "US");
                Tracks tracks = service.getArtistTopTrack(artistId, qMap);

                List<Track> tracksList = tracks.tracks;

                Vector<ContentValues> cVVector = new Vector<ContentValues>(tracksList.size());

                for (Track track : tracksList) {

                    //boolean is_playable = tracks.tracks.get(i).is_playable;
                    String id = track.id;
                    String name = track.name;
                    String preview_url = track.preview_url;
                    String album_name = track.album.name;
                    long duration = track.duration_ms;

                    List<Image> images = track.album.images;
                    String imageURL = Utils.getThumbnailImageURL(images);
                    String fullImageURL = Utils.getFullImageURL(images);
                    int popularity = track.popularity;

                    ContentValues trackValues = new ContentValues();
                    trackValues.put(SpotifyStreamerContract.TrackEntry.COLUMN_ARTIST_NAME_KEY, artistName);
                    trackValues.put(SpotifyStreamerContract.TrackEntry.COLUMN_TRACK_ID, id);
                    trackValues.put(SpotifyStreamerContract.TrackEntry.COLUMN_TRACK_NAME,name);
                    trackValues.put(SpotifyStreamerContract.TrackEntry.COLUMN_PREVIEW_URL, preview_url);
                    trackValues.put(SpotifyStreamerContract.TrackEntry.COLUMN_DURATION_IN_MS, duration);
                    trackValues.put(SpotifyStreamerContract.TrackEntry.COLUMN_ALBUM_NAME, album_name);
                    trackValues.put(SpotifyStreamerContract.TrackEntry.COLUMN_IMAGE_URL, imageURL);
                    trackValues.put(SpotifyStreamerContract.TrackEntry.COLUMN_FULL_IMAGE_URL, fullImageURL);
                    trackValues.put(SpotifyStreamerContract.TrackEntry.COLUMN_POPULARITY, popularity);

                    cVVector.add(trackValues);
                }

                //delete old data first
                int deleted = getContext().getContentResolver().delete(SpotifyStreamerContract.TrackEntry.CONTENT_URI, null, null);

                int inserted = 0;
                // add to database
                if ( cVVector.size() > 0 ) {

                    //bulk insert of top songs data
                    ContentValues[] cvArray = new ContentValues[cVVector.size()];
                    cVVector.toArray(cvArray);
                    inserted = getContext().getContentResolver().bulkInsert(SpotifyStreamerContract.TrackEntry.CONTENT_URI, cvArray);

                    TopSongsListingActivity.syncResult = true;
                    getContext().getContentResolver().notifyChange(Uri.parse(TopSongsListingActivity.SYNC_RESULT_URI), null, false);


                }
                Log.d(LOG_TAG, "SyncAdpater working is Complete. " + deleted + " Deleted");
                Log.d(LOG_TAG, "SyncAdpater working is Complete. " + inserted + " Inserted");

                if (cVVector.size() == 0) {
                    TopSongsListingActivity.syncResult = false;
                    getContext().getContentResolver().notifyChange(Uri.parse(TopSongsListingActivity.SYNC_RESULT_URI), null, false);
                }

            } catch (Exception e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
        }



        return;
    }


    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        TopSongsSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }
}