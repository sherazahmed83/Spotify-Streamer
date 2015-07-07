package com.spotifystreamer.app.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.format.Time;

/**
 * Created by Sheraz on 7/3/2015.
 */
public class SpotifyStreamerContract {

    public static final String CONTENT_AUTHORITY = "com.spotifystreamer.app";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_TRACK = "track";

    // To make it easy to query for the exact date, we normalize all dates that go into
    // the database to the start of the the Julian day at UTC.
    public static long normalizeDate(long startDate) {
        // normalize the start date to the beginning of the (UTC) day
        Time time = new Time();
        time.set(startDate);
        int julianDay = Time.getJulianDay(startDate, time.gmtoff);
        return time.setJulianDay(julianDay);
    }

    /* Inner class that defines the table contents of the location table */
    public static final class TrackEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRACK).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRACK;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRACK;

        // Table name
        public static final String TABLE_NAME = "track";

        public static final String COLUMN_TRACK_ID   = "track_id";
        public static final String COLUMN_TRACK_NAME = "track_name";
        public static final String COLUMN_ARTIST_NAME_KEY = "artist_name";
        public static final String COLUMN_PREVIEW_URL = "preview_url";
        public static final String COLUMN_DURATION_IN_MS = "duration_in_ms";
        public static final String COLUMN_ALBUM_NAME = "album_name";
        public static final String COLUMN_IMAGE_URL = "image_url";
        public static final String COLUMN_FULL_IMAGE_URL = "full_image_url";
        public static final String COLUMN_POPULARITY = "popularity";

        public static Uri buildTrackUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildTrackUriWithOutArtistName() {
            /**
             * "content://com.spotifystreamer.app/track"
             */
            return CONTENT_URI;
        }

        public static Uri buildTrackUriWithArtistName(String artistName) {
            /**
             * "content://com.spotifystreamer.app/track/christina%20Aguilera"
             */
            return CONTENT_URI.buildUpon().appendPath(artistName).build();
        }

        public static String getArtistNameFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }
}