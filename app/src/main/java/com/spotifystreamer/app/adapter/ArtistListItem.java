package com.spotifystreamer.app.adapter;

/**
 * Created by Sheraz on 6/7/2015.
 */
public class ArtistListItem {
    private String artist;
    private String imagePath;

    public ArtistListItem () {

    }

    public ArtistListItem (String artist, String imagePath) {
        this.artist = artist;
        this.imagePath = imagePath;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
