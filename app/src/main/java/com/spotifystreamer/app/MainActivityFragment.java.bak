package com.spotifystreamer.app;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import com.spotifystreamer.app.adapter.ArtistListItem;
import com.spotifystreamer.app.adapter.CustomListLazyAdapter;
import com.spotifystreamer.app.adapter.XMLParser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * A placeholder fragment containing a simple view.
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

    public MainActivityFragment() {
    }

    @Override
    public void onStart () {
        super.onStart();
        FetchMusicDataTask task = new FetchMusicDataTask();
        task.execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        adapter = new CustomListLazyAdapter(getActivity(), getSongsList());
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        inputSearch = (EditText) rootView.findViewById(R.id.inputSearch);
        listView = (ListView) rootView.findViewById(R.id.listview_artist);

        listView.setAdapter(adapter);

        /**
         * Enabling Search Filter
         * */
        inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                adapter.getFilter().filter(cs);
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

    private ArrayList<HashMap<String, String>> getSongs() {
        ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();

        XMLParser parser = new XMLParser();
        String xml = parser.getXmlFromUrl(URL); // getting XML from URL
        Document doc = parser.getDomElement(xml); // getting DOM element

        NodeList nl = doc.getElementsByTagName(KEY_SONG);
        // looping through all song nodes <song>
        for (int i = 0; i < nl.getLength(); i++) {
            // creating new HashMap
            HashMap<String, String> map = new HashMap<String, String>();
            Element e = (Element) nl.item(i);
            // adding each child node to HashMap key => value
            map.put(KEY_ID, parser.getValue(e, KEY_ID));
            map.put(KEY_TITLE, parser.getValue(e, KEY_TITLE));
            map.put(KEY_ARTIST, parser.getValue(e, KEY_ARTIST));
            map.put(KEY_DURATION, parser.getValue(e, KEY_DURATION));
            map.put(KEY_THUMB_URL, parser.getValue(e, KEY_THUMB_URL));

            // adding HashList to ArrayList
            songsList.add(map);
        }

        return songsList;
    }

    private ArrayList<ArtistListItem> getSongsList() {
        ArrayList<ArtistListItem> songsList = new ArrayList<ArtistListItem>();

        XMLParser parser = new XMLParser();
        String xml = parser.getXmlFromUrl(URL); // getting XML from URL
        Document doc = parser.getDomElement(xml); // getting DOM element

        NodeList nl = doc.getElementsByTagName(KEY_SONG);
        // looping through all song nodes <song>
        for (int i = 0; i < nl.getLength(); i++) {
            // creating new ArtistListItem
            ArtistListItem item = new ArtistListItem();
            Element e = (Element) nl.item(i);
            // adding each child node to HashMap key => value
            item.setArtist(parser.getValue(e, KEY_ARTIST));
            item.setImagePath(parser.getValue(e, KEY_THUMB_URL));

            // adding item to ArrayList
            songsList.add(item);
        }

        return songsList;
    }

    public class FetchMusicDataTask extends AsyncTask<String, Void, ArrayList<ArtistListItem>> {

        private final String LOG_TAG = FetchMusicDataTask.class.getSimpleName();


        @Override
        protected ArrayList<ArtistListItem> doInBackground(String[] params) {
            ArrayList<ArtistListItem> songsList = new ArrayList<ArtistListItem>();

            XMLParser parser = new XMLParser();
            String xml = parser.getXmlFromUrl(URL); // getting XML from URL
            Document doc = parser.getDomElement(xml); // getting DOM element

            NodeList nl = doc.getElementsByTagName(KEY_SONG);
            // looping through all song nodes <song>
            for (int i = 0; i < nl.getLength(); i++) {
                // creating new ArtistListItem
                ArtistListItem item = new ArtistListItem();
                Element e = (Element) nl.item(i);
                // adding each child node to HashMap key => value
                item.setArtist(parser.getValue(e, KEY_ARTIST));
                item.setImagePath(parser.getValue(e, KEY_THUMB_URL));

                // adding item to ArrayList
                songsList.add(item);
            }
            return songsList;
        }

        @Override
        protected void onPostExecute(ArrayList<ArtistListItem> items) {
            if (items != null) {
                adapter.clear();
                for (ArtistListItem item: items) {
                    adapter.add(item);
                }
            }
        }


    }
}
