package com.spotifystreamer.app;

/**
 * Created by Sheraz on 6/9/2015.
 */
public abstract class StoppableRunnable implements Runnable {

    private volatile boolean mIsStopped = false;

    public abstract void stoppableRun();

    public void run() {
        setStopped(false);
        while(!isStopped()) {
            stoppableRun();
            stop();
        }
    }

    public boolean isStopped() {
        return mIsStopped;
    }

    private void setStopped(boolean isStop) {
        if (mIsStopped != isStop)
            mIsStopped = isStop;
    }

    public void stop() {
        setStopped(true);
    }
}