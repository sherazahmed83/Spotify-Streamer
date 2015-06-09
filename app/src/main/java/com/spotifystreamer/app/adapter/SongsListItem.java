package com.spotifystreamer.app.adapter;

/**
 * Created by Sheraz on 6/8/2015.
 */
public class SongsListItem {
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
    private String id;
    private String name;
    private String previewURL;
    private Long durationInMS;
    private String albumName;
    private String imageURL;

    public SongsListItem(String id, String name, String previewURL, Long durationInMS, String albumName, String imageURL) {
        this.id = id;
        this.name = name;
        this.previewURL = previewURL;
        this.durationInMS = durationInMS;
        this.albumName = albumName;
        this.imageURL = imageURL;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPreviewURL() {
        return previewURL;
    }

    public void setPreviewURL(String previewURL) {
        this.previewURL = previewURL;
    }

    public Long getDurationInMS() {
        return durationInMS;
    }

    public void setDurationInMS(Long durationInMS) {
        this.durationInMS = durationInMS;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}
