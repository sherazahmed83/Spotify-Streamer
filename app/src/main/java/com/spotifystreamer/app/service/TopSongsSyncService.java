package com.spotifystreamer.app.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class TopSongsSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private String LOG_TAG = TopSongsSyncService.class.getSimpleName();
    private static TopSongsSyncAdapter sArtistsSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d(LOG_TAG, "onCreate - ArtistsSyncService");
        synchronized (sSyncAdapterLock) {
            if (sArtistsSyncAdapter == null) {
                sArtistsSyncAdapter = new TopSongsSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sArtistsSyncAdapter.getSyncAdapterBinder();
    }
}