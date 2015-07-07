package com.spotifystreamer.app.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.spotifystreamer.app.R;
import com.spotifystreamer.app.TopSongsListingActivityFragment;
import com.squareup.picasso.Picasso;

/**
 * Created by Sheraz on 6/8/2015.
 */
public class CustomSongsListAdapter extends
        CursorAdapter {

    public CustomSongsListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View view = LayoutInflater.from(context).inflate(R.layout.list_songs_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();


        String songTitle        = cursor.getString(TopSongsListingActivityFragment.COL_TRACK_NAME);
        String albumName        = cursor.getString(TopSongsListingActivityFragment.COL_ALBUM_NAME);
        String imageURL         = cursor.getString(TopSongsListingActivityFragment.COL_IMAGE_URL);
        String durationInMS_STR = cursor.getString(TopSongsListingActivityFragment.COL_DURATION_IN_MS);

        viewHolder.song_title.setText(songTitle);
        viewHolder.album_name.setText(albumName);

        Picasso.with(context).load(imageURL).into(viewHolder.thumb_image);

//        long durationInMS = Long.parseLong(durationInMS_STR);
//        long minutes = (durationInMS / 1000) / 60;
//        long seconds = (durationInMS / 1000) % 60;
//        if(seconds < 10) {
//            viewHolder.duration.setText(minutes + ":0" + seconds);
//        } else {
//            viewHolder.duration.setText(minutes + ":" + seconds);
//        }


    }

    public static class ViewHolder {
        public final ImageView thumb_image;
        public final TextView song_title;
        public final TextView album_name;
        //public final TextView duration;

        public ViewHolder(View vi) {
            thumb_image=(ImageView)vi.findViewById(R.id.list_image); // thumb image
            song_title = (TextView)vi.findViewById(R.id.title); // Song Title
            album_name = (TextView)vi.findViewById(R.id.album_name); // Album Name
            //duration = (TextView)vi.findViewById(R.id.duration); // Duration
        }
    }


}
