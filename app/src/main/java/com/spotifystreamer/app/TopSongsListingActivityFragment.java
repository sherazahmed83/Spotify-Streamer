package com.spotifystreamer.app;

import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.spotifystreamer.app.adapter.CustomSongsListAdapter;
import com.spotifystreamer.app.adapter.Utils;
import com.spotifystreamer.app.data.SpotifyStreamerContract;
import com.spotifystreamer.app.data.SpotifyStreamerContract.TrackEntry;

/**
 * A placeholder fragment containing a simple view.
 */
public class TopSongsListingActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = TopSongsListingActivityFragment.class.getSimpleName();
    private ListView mListView;
    private CustomSongsListAdapter mSongsListAdapter;
    private MenuItem menuItem;
    private String artistName = "";
    private static int mSelectedItemPosition = 0;
    private static String ITEM_POSITION;
    private static final int TRACKS_LOADER = 0;
    private boolean mTwoPane;
    private Toast toast = null;

    public static final String[] TRACK_COLUMNS = {
            TrackEntry.TABLE_NAME + "." + TrackEntry._ID,
            TrackEntry.COLUMN_ARTIST_NAME_KEY,
            TrackEntry.COLUMN_TRACK_ID,
            TrackEntry.COLUMN_TRACK_NAME,
            TrackEntry.COLUMN_PREVIEW_URL,
            TrackEntry.COLUMN_DURATION_IN_MS,
            TrackEntry.COLUMN_ALBUM_NAME,
            TrackEntry.COLUMN_IMAGE_URL,
            TrackEntry.COLUMN_FULL_IMAGE_URL,
            TrackEntry.COLUMN_POPULARITY
    };

    public static final int COL_TRACK_PK_ID = 0;
    public static final int COL_ARTIST_NAME_KEY = 1;
    public static final int COL_TRACK_ID   = 2;
    public static final int COL_TRACK_NAME = 3;
    public static final int COL_PREVIEW_URL = 4;
    public static final int COL_DURATION_IN_MS = 5;
    public static final int COL_ALBUM_NAME = 6;
    public static final int COL_IMAGE_URL = 7;
    public static final int COL_FULL_IMAGE_URL = 8;
    public static final int COL_POPULATRITY = 9;



    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        final Context context = container.getContext();

        String artistId = "";
        mSongsListAdapter = new CustomSongsListAdapter(getActivity(), null, 0);

        Bundle arguments = getArguments();
        if (arguments != null) {
            artistId = arguments.getString(MainActivityFragment.ARTIST_ID_EXTRA_STRING);
            artistName = arguments.getString(MainActivityFragment.ARTIST_NAME_EXTRA_STRING);

            // Just to prevent the Loader to load the Songs list fragment container at
            // start up to show empty screen
            if (getLoaderManager().getLoader(TRACKS_LOADER) == null) {
                getLoaderManager().initLoader(TRACKS_LOADER, null, this);
            }
        }

        if (mTwoPane) {
            ActionBar bar = ((MainActivity) getActivity()).getSupportActionBar();
            bar.setTitle(getString(R.string.app_name));
            bar.setSubtitle(artistName);

            ContentObserver mObserver = new ContentObserver(new Handler()) {
                public void onChange(boolean selfChange) {

                    if (!TopSongsListingActivity.syncResult) {
                        toast = Toast.makeText(getActivity(), getString(R.string.no_songs_found_message), Toast.LENGTH_SHORT);
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
            context.getContentResolver().registerContentObserver(Uri.parse(TopSongsListingActivity.SYNC_RESULT_URI), false, mObserver);
        } else {
            ActionBar bar = null;
            try {
                bar = ((TopSongsListingActivity) getActivity()).getSupportActionBar();
                bar.setTitle(getString(R.string.title_activity_top_songs_listing));
            } catch (ClassCastException e) {
                bar = ((MainActivity) getActivity()).getSupportActionBar();
                bar.setTitle(getString(R.string.app_name));

                ContentObserver mObserver = new ContentObserver(new Handler()) {
                    public void onChange(boolean selfChange) {

                        if (!TopSongsListingActivity.syncResult) {
                            toast = Toast.makeText(context.getApplicationContext(), getString(R.string.no_songs_found_message), Toast.LENGTH_SHORT);
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
                context.getContentResolver().registerContentObserver(Uri.parse(TopSongsListingActivity.SYNC_RESULT_URI), false, mObserver);
            }

            bar.setSubtitle(artistName);
        }

        View rootView =  inflater.inflate(R.layout.fragment_top_songs_listing, container, false);
        mListView = (ListView) rootView.findViewById(R.id.listview_songs);
        mListView.setAdapter(mSongsListAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, final View view,
                                    int position, long id) {
                final Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if(cursor != null) {

                    if (mTwoPane) {
                        SongPlayerActivityFragment fragment = new SongPlayerActivityFragment();
                        Bundle arguments = new Bundle();
                        arguments.putStringArray(SongPlayerActivityFragment.TRACK_META_DATA, new String[]{cursor.getString(COL_TRACK_ID), cursor.getString(COL_ARTIST_NAME_KEY), cursor.getString(COL_ALBUM_NAME),
                                cursor.getString(COL_TRACK_NAME), cursor.getString(COL_DURATION_IN_MS), cursor.getString(COL_FULL_IMAGE_URL), cursor.getString(COL_PREVIEW_URL),
                                String.valueOf(mTwoPane)});
                        fragment.setArguments(arguments);
                        fragment.show(getActivity().getSupportFragmentManager(), SongPlayerActivity.SONG_PLAYER_DIALOG_TAG);

                    } else {
                        Intent intent = new Intent(view.getContext(), SongPlayerActivity.class);
                        intent.putExtra(Intent.EXTRA_TEXT, new String[]{cursor.getString(COL_TRACK_ID), cursor.getString(COL_ARTIST_NAME_KEY), cursor.getString(COL_ALBUM_NAME),
                                cursor.getString(COL_TRACK_NAME), cursor.getString(COL_DURATION_IN_MS), cursor.getString(COL_FULL_IMAGE_URL), cursor.getString(COL_PREVIEW_URL),
                                String.valueOf(mTwoPane)});
                        startActivity(intent);
                    }
                }
                mSelectedItemPosition = position;
            }

        });


        if (savedInstanceState != null && savedInstanceState.containsKey(ITEM_POSITION)) {
            mSelectedItemPosition = savedInstanceState.getInt(ITEM_POSITION);
        }

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String sortOrder = TrackEntry.COLUMN_POPULARITY + " DESC";
        Uri trackUri = SpotifyStreamerContract.TrackEntry.buildTrackUriWithOutArtistName();

        return new CursorLoader(getActivity(),
                trackUri,
                TRACK_COLUMNS,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        try {
            Log.d(LOG_TAG, (cursor != null) + "  " + cursor.moveToFirst());
            if(!cursor.moveToFirst() && Utils.isContentChanged()) {
                Toast.makeText(getActivity().getApplicationContext(),
                        "No Tracks found for this artist!", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e) {
            Log.d(LOG_TAG, (cursor == null) + "");
        }
        mSongsListAdapter.swapCursor(cursor);
        if (mSelectedItemPosition != ListView.INVALID_POSITION) {
            mListView.smoothScrollToPosition(mSelectedItemPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mSongsListAdapter.swapCursor(null);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mSelectedItemPosition != ListView.INVALID_POSITION) {
            outState.putInt(ITEM_POSITION, mSelectedItemPosition);
        }

        outState.putBoolean("activity_destroyed", true);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            boolean activityDestroyed = savedInstanceState.getBoolean("activity_destroyed");
            if (activityDestroyed) {
                getLoaderManager().initLoader(TRACKS_LOADER, null, this);
            }
        }

    }

    public void setTwoPane(boolean mTwoPane) {
        this.mTwoPane = mTwoPane;
    }


}
