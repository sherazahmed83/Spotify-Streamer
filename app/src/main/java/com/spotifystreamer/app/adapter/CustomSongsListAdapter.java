package com.spotifystreamer.app.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.spotifystreamer.app.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Sheraz on 6/8/2015.
 */
public class CustomSongsListAdapter extends
        BaseAdapter {

    private Activity activity;
    private ArrayList<SongsListItem> mOriginalValues;
    private ArrayList<SongsListItem> mObjects;
    private static LayoutInflater inflater = null;
//    public ImageLoader imageLoader;

    /**
     * Indicates whether or not {@link #notifyDataSetChanged()} must be called whenever
     * {@link #mObjects} is modified.
     */
    private boolean mNotifyOnChange = true;
    /**
     * Lock used to modify the content of {@link #mObjects}. Any write operation
     * performed on the array should be synchronized on this lock.
     */
    private final Object mLock = new Object();

    public CustomSongsListAdapter(Activity activity, ArrayList<SongsListItem> data) {
        this.activity = activity;
        this.mObjects = data;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        imageLoader  = new ImageLoader(activity.getApplicationContext());
    }

    public int getCount() {
        return mObjects.size();
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;

        if (convertView == null) {
            vi = inflater.inflate(R.layout.list_songs_row, null);
        }

        ImageView thumb_image=(ImageView)vi.findViewById(R.id.list_image); // thumb image
        TextView song_title = (TextView)vi.findViewById(R.id.title); // Song Title
        TextView album_name = (TextView)vi.findViewById(R.id.album_name); // Album Name
        TextView duration = (TextView)vi.findViewById(R.id.duration); // Duration

        SongsListItem songListItem = getItem(position);

        // Setting all values in listview
        song_title.setText(songListItem.getName());
        album_name.setText(songListItem.getAlbumName());
        Picasso.with(activity).load(songListItem.getImageURL()).into(thumb_image);
//        imageLoader.DisplayImage(songListItem.getImageURL(), thumb_image);
        long durationInMS = songListItem.getDurationInMS();
        long minutes = (durationInMS / 1000) / 60;
        long seconds = (durationInMS / 1000) % 60;
        if(seconds < 10) {
            duration.setText(minutes + ":0" + seconds);
        } else {
            duration.setText(minutes + ":" + seconds);
        }
        return vi;
    }

    /**
     * {@inheritDoc}
     */
    public void clear() {
        synchronized (mLock) {
            if (mOriginalValues != null) {
                mOriginalValues.clear();
            } else {
                mObjects.clear();
            }
        }
        if (mNotifyOnChange)  notifyDataSetChanged();
    }

    /**
     * Adds the specified object at the end of the array.
     *
     * @param object The object to add at the end of the array.
     */
    public void add(SongsListItem object) {
        synchronized (mLock) {
            if (mOriginalValues != null) {
                mOriginalValues.add(object);
            } else {
                mObjects.add(object);
            }
        }
        if (mNotifyOnChange) notifyDataSetChanged();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        mNotifyOnChange = true;
    }

    /**
     * {@inheritDoc}
     */
    public SongsListItem getItem(int position) {
        return mObjects.get(position);
    }

    /**
     * Returns the position of the specified item in the array.
     *
     * @param item The item to retrieve the position of.
     *
     * @return The position of the specified item.
     */
    public int getPosition(SongsListItem item) {
        return mObjects.indexOf(item);
    }
}
