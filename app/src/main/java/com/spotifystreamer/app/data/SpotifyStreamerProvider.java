package com.spotifystreamer.app.data;

/**
 * Created by Sheraz on 6/11/2015.
 */
import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import com.spotifystreamer.app.data.SpotifyStreamerContract.TrackEntry;

public class SpotifyStreamerProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private SpotifyStreamerDbHelper mOpenHelper;

    static final int TRACK = 100;
    static final int TRACK_WITH_ARTIST_NAME = 101;

    private static final SQLiteQueryBuilder sWeatherByLocationSettingQueryBuilder;

    static{
        sWeatherByLocationSettingQueryBuilder = new SQLiteQueryBuilder();

        sWeatherByLocationSettingQueryBuilder.setTables(
                TrackEntry.TABLE_NAME);
    }

    //track.artist_name = ?
    private static final String sArtistNameSelection =
            TrackEntry.TABLE_NAME +
                    "." + TrackEntry.COLUMN_ARTIST_NAME_KEY + " = ? ";

//    //location.location_setting = ? AND date >= ?
//    private static final String sLocationSettingWithStartDateSelection =
//            WeatherContract.LocationEntry.TABLE_NAME+
//                    "." + WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ? AND " +
//                    WeatherContract.WeatherEntry.COLUMN_DATE + " >= ? ";
//
//    //location.location_setting = ? AND date = ?
//    private static final String sLocationSettingAndDaySelection =
//            WeatherContract.LocationEntry.TABLE_NAME +
//                    "." + WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ? AND " +
//                    WeatherContract.WeatherEntry.COLUMN_DATE + " = ? ";

    private Cursor getTracksByArtistName(Uri uri, String[] projection, String sortOrder) {
        String artistName = TrackEntry.getArtistNameFromUri(uri);
//        long startDate = WeatherContract.WeatherEntry.getStartDateFromUri(uri);

        String[] selectionArgs;
        String selection;

//        if (startDate == 0) {
            selection = sArtistNameSelection;
            selectionArgs = new String[]{artistName};
//        } else {
//            selectionArgs = new String[]{locationSetting, Long.toString(startDate)};
//            selection = sLocationSettingWithStartDateSelection;
//        }

        return sWeatherByLocationSettingQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    static UriMatcher buildUriMatcher() {
        // 1) The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case. Add the constructor below.
        final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = SpotifyStreamerContract.CONTENT_AUTHORITY;

        // 2) Use the addURI function to match each of the types.  Use the constants from
        // SpotifyStreamerContract to help define the types to the UriMatcher.
        sURIMatcher.addURI(authority, SpotifyStreamerContract.PATH_TRACK,TRACK);
        sURIMatcher.addURI(authority, SpotifyStreamerContract.PATH_TRACK + "/*",TRACK_WITH_ARTIST_NAME);

        // 3) Return the new matcher!
        return sURIMatcher;
    }

    /*
        Students: We've coded this for you.  We just create a new WeatherDbHelper for later use
        here.
     */
    @Override
    public boolean onCreate() {
        mOpenHelper = new SpotifyStreamerDbHelper(getContext());
        return true;
    }

    /*
        Students: Here's where you'll code the getType function that uses the UriMatcher.  You can
        test this by uncommenting testGetType in TestProvider.
     */
    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case TRACK:
                return TrackEntry.CONTENT_TYPE;
            case TRACK_WITH_ARTIST_NAME:
                return TrackEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // "track/*"
            case TRACK_WITH_ARTIST_NAME: {
                retCursor = getTracksByArtistName(uri, projection, sortOrder);
                break;
            }
            // "track"
            case TRACK: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        TrackEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    /*
        Student: Add the ability to insert Locations to the implementation of this function.
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case TRACK: {
                normalizeDate(values);
                long _id = db.insert(TrackEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = TrackEntry.buildTrackUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Student: Start by getting a writable database
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";

        switch (match) {
            case TRACK: {
                rowsDeleted = db.delete(
                        TrackEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        // Student: return the actual rows deleted
        return rowsDeleted;
    }

    private void normalizeDate(ContentValues values) {
        // normalize the date value
//        if (values.containsKey(WeatherContract.WeatherEntry.COLUMN_DATE)) {
//            long dateValue = values.getAsLong(WeatherContract.WeatherEntry.COLUMN_DATE);
//            values.put(WeatherContract.WeatherEntry.COLUMN_DATE, WeatherContract.normalizeDate(dateValue));
//        }
    }

    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // Student: This is a lot like the delete function.  We return the number of rows impacted
        // by the update.
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case TRACK:
                normalizeDate(values);
                rowsUpdated = db.update(TrackEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TRACK:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        normalizeDate(value);
                        long _id = db.insert(TrackEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}