package com.spotifystreamer.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.spotifystreamer.app.adapter.CustomSongsListAdapter;
import com.spotifystreamer.app.adapter.SongsListItem;
import com.spotifystreamer.app.adapter.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;


/**
 * A placeholder fragment containing a simple view.
 */
public class TopSongsListingActivityFragment extends Fragment {

    private final String LOG_TAG = TopSongsListingActivityFragment.class.getSimpleName();
    private ListView listView;
    private CustomSongsListAdapter adapter;
    private MenuItem menuItem;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        String artistId = "";
        String artistName = "";

        adapter = new CustomSongsListAdapter(getActivity(), new ArrayList<SongsListItem>());
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            String data[] = intent.getStringArrayExtra(Intent.EXTRA_TEXT);
            artistId = data[0];
            artistName = data[1];
        }
        ActionBar bar = ((TopSongsListingActivity) getActivity()).getSupportActionBar();
        bar.setTitle(getString(R.string.title_activity_top_songs_listing));
        bar.setSubtitle(artistName);

        View rootView =  inflater.inflate(R.layout.fragment_top_songs_listing, container, false);
        listView = (ListView) rootView.findViewById(R.id.listview_songs);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
//                final ArtistListItem item = (ArtistListItem) parent.getItemAtPosition(position);
//                Intent intent = new Intent(view.getContext(), TopSongsListingActivity.class);
//                intent.putExtra(Intent.EXTRA_TEXT, new String[]{item.getId(), item.getArtist()});
//                startActivity(intent);

            }

        });
        FetchSpotifyTopSongsTask task = new FetchSpotifyTopSongsTask();
        task.execute(artistId);


        return rootView;
    }


    public class FetchSpotifyTopSongsTask extends AsyncTask<String, Void, ArrayList<SongsListItem>> {

        private final String LOG_TAG = FetchSpotifyTopSongsTask.class.getSimpleName();
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
        protected ArrayList<SongsListItem> doInBackground(String[] params) {
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
            Map<String, Object> qMap = new HashMap<String, Object>();
            qMap.put(SpotifyService.COUNTRY, "US");
            Tracks tracks = service.getArtistTopTrack(params[0], qMap);

            List<Track> tracksList = tracks.tracks;

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

            ArrayList<SongsListItem> list = new ArrayList<SongsListItem>();

            for (Track track : tracksList) {

                //boolean is_playable = tracks.tracks.get(i).is_playable;
                String id = track.id;
                String name = track.name;
                String preview_url = track.preview_url;
                String album_name = track.album.name;
                long duration = track.duration_ms;

                List<Image> images = track.album.images;
                String imageURL = Utils.getImageURL(images);

                SongsListItem item = new SongsListItem(id, name, preview_url, duration, album_name, imageURL);
                list.add(item);
            }

            return list;
        }

        @Override
        protected void onPostExecute(ArrayList<SongsListItem> list) {
            if (null != pDialog && pDialog.isShowing()) {
                pDialog.dismiss();
            }

            if (list != null && list.size() > 0) {

                adapter.clear();
                for (SongsListItem item: list) {
                    adapter.add(item);
                }

            } else {
                final Activity activity = getActivity();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast toast = Toast.makeText(activity, activity.getString(R.string.search_no_song_found), Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                });
            }
        }
    }
}
