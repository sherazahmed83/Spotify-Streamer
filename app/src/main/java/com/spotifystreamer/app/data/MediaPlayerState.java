package com.spotifystreamer.app.data;

import java.io.Serializable;

/**
 * Created by Sheraz on 7/8/2015.
 */
public class MediaPlayerState implements Serializable {

    private boolean isPlaying;
    private double trackDuration;
    private int trackCurrentPosition;
    private String trackDataSource;

    public MediaPlayerState() {

    }

    public MediaPlayerState(boolean isPlaying, double trackDuration, int trackCurrentPosition, String trackDataSource) {
        this.isPlaying = isPlaying;
        this.trackDuration = trackDuration;
        this.trackCurrentPosition = trackCurrentPosition;
        this.trackDataSource = trackDataSource;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setIsPlaying(boolean isPlaying) {
        this.isPlaying = isPlaying;
    }

    public double getTrackDuration() {
        return trackDuration;
    }

    public void setTrackDuration(double trackDuration) {
        this.trackDuration = trackDuration;
    }

    public int getTrackCurrentPosition() {
        return trackCurrentPosition;
    }

    public void setTrackCurrentPosition(int trackCurrentPosition) {
        this.trackCurrentPosition = trackCurrentPosition;
    }

    public String getTrackDataSource() {
        return trackDataSource;
    }

    public void setTrackDataSource(String trackDataSource) {
        this.trackDataSource = trackDataSource;
    }
}
