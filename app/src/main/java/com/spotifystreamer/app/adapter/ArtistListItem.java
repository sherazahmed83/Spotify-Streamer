package com.spotifystreamer.app.adapter;

/**
 * Created by Sheraz on 6/7/2015.
 */
public class ArtistListItem {
    private String artist;
    private String imageURL;
    private String id;

    public ArtistListItem (String artist, String imageURL, String id) {
        this.artist = artist;
        this.imageURL = imageURL;
        this.id = id;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
