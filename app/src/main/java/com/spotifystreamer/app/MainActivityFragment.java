package com.spotifystreamer.app;

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

import com.spotify.sdk.android.player.Player;
import com.spotifystreamer.app.adapter.ArtistListItem;
import com.spotifystreamer.app.adapter.CustomListLazyAdapter;

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
    // All static variables
    static final String URL = "http://api.androidhive.info/music/music.xml";
    // XML node keys
    static final String KEY_SONG = "song"; // parent node
    static final String KEY_ID = "id";
    static final String KEY_TITLE = "title";
    static final String KEY_ARTIST = "artist";
    static final String KEY_DURATION = "duration";
    static final String KEY_THUMB_URL = "thumb_url";

    private CustomListLazyAdapter adapter;
    private Player mPlayer;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        adapter = new CustomListLazyAdapter(getActivity(), new ArrayList<ArtistListItem>());
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        inputSearch = (EditText) rootView.findViewById(R.id.inputSearch);
        listView = (ListView) rootView.findViewById(R.id.listview_artist);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                final ArtistListItem item = (ArtistListItem) parent.getItemAtPosition(position);
//                Intent intent = new Intent(view.getContext(), DetailActivity.class);
//                intent.putExtra(Intent.EXTRA_TEXT, forecast);
//                startActivity(intent);

                Toast toast = Toast.makeText(view.getContext(), item.getArtist(), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP|Gravity.LEFT, 0, 0);
                toast.show();
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


        @Override
        protected ArrayList<ArtistListItem> doInBackground(String[] params) {
            if (params == null || params.length == 0) {
                return null;
            }

            if (params.length == 1 && params[0].trim().equals("")) {
                return null;
            }

            SpotifyApi api = new SpotifyApi();

            api.setAccessToken(SPOTIFY_ACCESS_TOKEN);
            SpotifyService service = api.getService();
            ArtistsPager pager = service.searchArtists(params[0]);

            /**
             * Tracks tracks = service.getArtistTopTrack();
             *
             * get "is_playable" of the track (if available)
             * tracks.tracks.get(0).is_playable;
             *
             * get "id" of the track
             * tracks.tracks.get(0).id;
             *
             * get "name" of the track
             * tracks.tracks.get(0).name;
             *
             * get "preview_url" of the track
             * tracks.tracks.get(0).preview_url;
             *
             * get "duration_ms" of the track
             * tracks.tracks.get(0).duration_ms;
             *
             * get "album_name" of the track
             * tracks.tracks.get(0).album.name
             *
             * get "images" of the track
             * tracks.tracks.get(0).album.images
             *
             */


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
                for(Image image: images) {
                    if(image.height != null && image.height < 100) {
                        imageURL = image.url;
                        break;
                    }
                }

                if ((imageURL == null || imageURL.equals("")) && images.size() > 0) {
                    imageURL = images.get(0).url;
                }

                ArtistListItem artist = new ArtistListItem(artistName, imageURL, id);
                list.add(artist);

            }
            return list;
        }

        @Override
        protected void onPostExecute(ArrayList<ArtistListItem> list) {
            if (list != null) {
                adapter.clear();
                for (ArtistListItem item: list) {
                    adapter.add(item);
                }
            }
        }
    }
}
