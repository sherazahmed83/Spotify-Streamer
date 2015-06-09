package com.spotifystreamer.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.spotifystreamer.app.adapter.ArtistListItem;
import com.spotifystreamer.app.adapter.CustomArtistListAdapter;
import com.spotifystreamer.app.adapter.Utils;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Image;


/**
 * A placeholder fragment containing a simple view.
 *
 * 1- The UI is built with the help of
 *    http://www.androidhive.info/2012/02/android-custom-listview-with-image-and-text/
 *
 * 2- GitHub help
 *    https://help.github.com/articles/fork-a-repo/
 *    http://www.slideshare.net/GameCraftBulgaria/github-basics
 *
 * 3- Now adding the Spotify support
 *
 */
public class MainActivityFragment extends Fragment {

    // Search EditText
    private EditText inputSearch;
    private ListView listView;

    private CustomArtistListAdapter adapter;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        adapter = new CustomArtistListAdapter(getActivity(), new ArrayList<ArtistListItem>());
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        inputSearch = (EditText) rootView.findViewById(R.id.inputSearch);
        listView = (ListView) rootView.findViewById(R.id.listview_artist);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                final ArtistListItem item = (ArtistListItem) parent.getItemAtPosition(position);
                Intent intent = new Intent(view.getContext(), TopSongsListingActivity.class);
                intent.putExtra(Intent.EXTRA_TEXT, new String[] {item.getId(), item.getArtist()});
                startActivity(intent);

            }

        });


        /**
         * Enabling Search Filter
         * */
        inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                FetchSpotifyArtistsTask task = new FetchSpotifyArtistsTask();
                task.execute(cs.toString());

                // When user changed the Text
                // adapter.getFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });

        return rootView;
    }

    public class FetchSpotifyArtistsTask extends AsyncTask<String, Void, ArrayList<ArtistListItem>> {

        private final String LOG_TAG = FetchSpotifyArtistsTask.class.getSimpleName();
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Loading...");
            pDialog.setCancelable(true);
            pDialog.show();

        }

        @Override
        protected ArrayList<ArtistListItem> doInBackground(String[] params) {
            if (params == null || params.length == 0) {
                return null;
            }

            if (params.length == 1 && params[0].trim().equals("")) {
                return null;
            }

            if (Utils.SPOTIFY_ACCESS_TOKEN == null || Utils.SPOTIFY_ACCESS_TOKEN.trim().equals("")) {
                return null;
            }

            SpotifyApi api = new SpotifyApi();

            api.setAccessToken(Utils.SPOTIFY_ACCESS_TOKEN);
            SpotifyService service = api.getService();
            ArtistsPager pager = service.searchArtists(params[0]);

            List<Artist> items = pager.artists.items;
            /**
             * get "name" for text_view
             * get "images" list for image_view with attributes (height, width, url)
             * get "id" for fetching the artist when the user select it
             */

            ArrayList<ArtistListItem> list = new ArrayList<ArtistListItem>();

            for(Artist item: items) {
                String artistName = item.name;
                String id         = item.id;
                String imageURL   = null;

                List<Image> images = item.images;
                imageURL = Utils.getImageURL(images);

                ArtistListItem artist = new ArtistListItem(artistName, imageURL, id);
                list.add(artist);

            }
            return list;
        }

        @Override
        protected void onPostExecute(ArrayList<ArtistListItem> list) {
            if (null != pDialog && pDialog.isShowing()) {
                pDialog.dismiss();
            }

            if (list != null && list.size() > 0) {

                adapter.clear();
                for (ArtistListItem item: list) {
                    adapter.add(item);
                }

            } else {
                final Activity activity = getActivity();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.clear();
                        Toast toast = Toast.makeText(activity, activity.getString(R.string.search_no_artist_found), Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                });

            }
        }
    }
}
