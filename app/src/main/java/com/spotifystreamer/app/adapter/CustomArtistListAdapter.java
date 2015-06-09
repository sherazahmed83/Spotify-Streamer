package com.spotifystreamer.app.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.spotifystreamer.app.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


/**
 * Created by Sheraz on 6/7/2015.
 */

public class CustomArtistListAdapter extends
        BaseAdapter  implements Filterable {

    private Activity activity;
    private ArrayList<ArtistListItem> mOriginalValues;
    private ArrayList<ArtistListItem> mObjects;
    private static LayoutInflater inflater = null;
//    public ImageLoader imageLoader;
    private ArrayFilter mFilter;
    /**
     * Indicates whether or not {@link #notifyDataSetChanged()} must be called whenever
     * {@link #mObjects} is modified.
     */
    private boolean mNotifyOnChange = true;
    /**
     * Lock used to modify the content of {@link #mObjects}. Any write operation
     * performed on the array should be synchronized on this lock. This lock is also
     * used by the filter (see {@link #getFilter()} to make a synchronized copy of
     * the original array of data.
     */
    private final Object mLock = new Object();


    public CustomArtistListAdapter(Activity activity, ArrayList<ArtistListItem> data) {
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
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.list_item, null);

        ImageView thumb_image=(ImageView)vi.findViewById(R.id.listitem_image); // thumb image
        TextView artist = (TextView)vi.findViewById(R.id.listitem_text); // artist name

        ArtistListItem artistListItem = getItem(position);

        // Setting all values in listview
        artist.setText(artistListItem.getArtist());
        Picasso.with(activity).load(artistListItem.getImageURL()).into(thumb_image);
//        imageLoader.DisplayImage(artistListItem.getImagePath(), thumb_image);
        return vi;
    }

    /**
     * {@inheritDoc}
     */
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new ArrayFilter();
        }
        return mFilter;
    }

    /**
     * <p>An array filter constrains the content of the array adapter with
     * a prefix. Each item that does not start with the supplied prefix
     * is removed from the list.</p>
     */
    private class ArrayFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();

            if (mOriginalValues == null) {
                synchronized (mLock) {
                    mOriginalValues = new ArrayList<ArtistListItem>(mObjects);
                }
            }

            if (prefix == null || prefix.length() == 0) {
                ArrayList<ArtistListItem> list;
                synchronized (mLock) {
                    list = new ArrayList<ArtistListItem>(mOriginalValues);
                }
                results.values = list;
                results.count = list.size();
            } else {
                String prefixString = prefix.toString().toLowerCase();

                ArrayList<ArtistListItem> values;
                synchronized (mLock) {
                    values = new ArrayList<ArtistListItem>(mOriginalValues);
                }

                final int count = values.size();
                final ArrayList<ArtistListItem> newValues = new ArrayList<ArtistListItem>();

                for (int i = 0; i < count; i++) {
                    final ArtistListItem value = values.get(i);
                    final String valueText = value.getArtist().toString().toLowerCase();

                    // First match against the whole, non-splitted value
                    if (valueText.startsWith(prefixString)) {
                        newValues.add(value);
                    } else {
                        final String[] words = valueText.split(" ");
                        final int wordCount = words.length;

                        // Start at index 0, in case valueText starts with space(s)
                        for (int k = 0; k < wordCount; k++) {
                            if (words[k].startsWith(prefixString)) {
                                newValues.add(value);
                                break;
                            }
                        }
                    }
                }

                results.values = newValues;
                results.count = newValues.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            //noinspection unchecked
            mObjects = (ArrayList<ArtistListItem>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
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
    public void add(ArtistListItem object) {
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
    public ArtistListItem getItem(int position) {
        return mObjects.get(position);
    }

    /**
     * Returns the position of the specified item in the array.
     *
     * @param item The item to retrieve the position of.
     *
     * @return The position of the specified item.
     */
    public int getPosition(ArtistListItem item) {
        return mObjects.indexOf(item);
    }
}

