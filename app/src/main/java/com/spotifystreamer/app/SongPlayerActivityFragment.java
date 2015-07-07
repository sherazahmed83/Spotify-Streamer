package com.spotifystreamer.app;


import android.annotation.TargetApi;
import android.app.Dialog;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.spotifystreamer.app.adapter.Utils;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * How to add a Toolbar to a DialogFragment
 *
 * http://www.truiton.com/2015/04/android-action-bar-dialog-using-toolbar/
 *
 */

/**
 * A placeholder fragment containing a simple view.
 */
public class SongPlayerActivityFragment extends DialogFragment/*  implements
        PlayerNotificationCallback, ConnectionStateCallback*/ {


    private final String LOG_TAG = SongPlayerActivityFragment.class.getSimpleName();

    // constants to transfer data from Activity to Fragement
    public static final String TRACK_META_DATA = "track_meta";
    private Runnable notification = null;
    private boolean mTwoPane;

    // UI components
    private ImageButton button_prev;

    private ImageButton button_forward;
    private ImageView   imageView;
    private TextView    textview_song_name;
    private TextView    textview_time_start;
    private TextView    textview_time_end;
    private TextView    textview_artist_name;
    private TextView    textview_album_name;
    ImageButton button_play;
    SeekBar     seekbar;

    // Data variables
    private String      trackId;
    private String      artistName;
    private String      albumName;
    private String      trackName;
    private Long        durationInMS;
    private String      imageURL;
    private String      previewURL;

    // seekbar interaction variables
    double      startTime = 0;
    private double      finalTime = 0;
    private int forwardTime = 5000;
    private int backwardTime = 5000;

//    private Player mPlayer;
    private MediaPlayer mPlayer;
    private final Handler mHandler = new Handler();

    public SongPlayerActivityFragment() {
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_song_player, null, false);

        Bundle arguments = getArguments();
        if (arguments != null) {
            String dataArray[] = arguments.getStringArray(TRACK_META_DATA);
            trackId = dataArray[0];
            artistName = dataArray[1];
            albumName = dataArray[2];
            trackName = dataArray[3];
            durationInMS = Long.parseLong(dataArray[4]);
            imageURL = dataArray[5];
            previewURL = dataArray[6];
            setTwoPane(Boolean.valueOf(dataArray[7]));
        }

        if (mTwoPane) {
            hideActionBarOnDialogFragment(rootView);
        } else {
            showActionBarOnDialogFragment(rootView);
        }


        mPlayer = getMediaPlayer();
        initializeUIReferences(rootView);
        initializeClickListeners();


        return rootView;
    }
    public MediaPlayer getMediaPlayer() {

        mPlayer = new MediaPlayer();
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                seekbar.setSecondaryProgress(percent);
            }
        });

        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                button_play.setImageResource(android.R.drawable.ic_media_play);
            }
        });

        try {
            mPlayer.setDataSource(previewURL);

        } catch (IllegalStateException e) {
            Log.e(LOG_TAG, "You might not set the URI correctly!");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return mPlayer;

    } // end fo getMediaPlayer



    /**
     * initialize all the UI references to be used inside this class
     */
    private void initializeUIReferences(View rootView) {

        button_prev          = (ImageButton) rootView.findViewById(R.id.button_prev);
        button_play          = (ImageButton) rootView.findViewById(R.id.button_play);
        button_forward       = (ImageButton) rootView.findViewById(R.id.button_forward);
        imageView            = (ImageView)   rootView.findViewById(R.id.imageView);

        textview_time_start  = (TextView)    rootView.findViewById(R.id.textview_time_start);
        textview_time_end    = (TextView)    rootView.findViewById(R.id.textview_time_end);
        textview_song_name   = (TextView)    rootView.findViewById(R.id.textview_song_name);
        textview_artist_name = (TextView)    rootView.findViewById(R.id.textview_artist_name);
        textview_album_name  = (TextView)    rootView.findViewById(R.id.textView_album_name);
        seekbar              = (SeekBar)     rootView.findViewById(R.id.seekBar);

        seekbar.setClickable(false);
        Picasso.with(getActivity()).load(imageURL).into(imageView);
        textview_song_name.setText(trackName);
        textview_artist_name.setText(artistName);
        textview_album_name.setText(albumName);

    }// End of initializeUIReferences()


    /**
     * initialize all click listeners
     */
    private void initializeClickListeners() {

        button_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    mPlayer.prepare();
                } catch (IllegalStateException e) {
                    Log.e(LOG_TAG, "You might not set the URI correctly!");
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                finalTime = mPlayer.getDuration();
                startTime = mPlayer.getCurrentPosition();

                textview_time_end.setText(String.format("%s:%s",
                                Utils.getTimeFormattedString(TimeUnit.MILLISECONDS.toMinutes((long) finalTime)),
                                Utils.getTimeFormattedString(TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) finalTime))))
                );

                textview_time_start.setText(String.format("%s:%s",
                                Utils.getTimeFormattedString(TimeUnit.MILLISECONDS.toMinutes((long) startTime)),
                                Utils.getTimeFormattedString(TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) startTime))))
                );


                if (!mPlayer.isPlaying()) {
                    mPlayer.start();
                    button_play.setImageResource(android.R.drawable.ic_media_pause);
                } else {
                    mPlayer.pause();
                    button_play.setImageResource(android.R.drawable.ic_media_play);
                }
                primarySeekBarProgressUpdater();
            }
        });

        // End of button_play configuration

        button_forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int temp = (int) startTime;

                if ((temp + forwardTime) <= finalTime) {
                    startTime = startTime + forwardTime;
                    mPlayer.seekTo((int) startTime);
                }
            }
        });


        button_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int temp = (int) startTime;

                if ((temp - backwardTime) > 0) {
                    startTime = startTime - backwardTime;
                    mPlayer.seekTo((int) startTime);
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Cannot jump backward 5 seconds", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // End of button_prev configuration
    }// End of initializeClickListeners() method

    private void primarySeekBarProgressUpdater() {
        if (mPlayer == null) {
            mHandler.removeCallbacks(notification);
            return;
        }
        try {

            int seekBarProgress = (int) (((float) mPlayer.getCurrentPosition() / finalTime) * 100);
            seekbar.setProgress(seekBarProgress); // This math construction give a percentage of "was playing"/"song length"

            textview_time_start.setText(String.format("%s:%s",
                            Utils.getTimeFormattedString(TimeUnit.MILLISECONDS.toMinutes((long) mPlayer.getCurrentPosition())),
                            Utils.getTimeFormattedString(TimeUnit.MILLISECONDS.toSeconds((long) mPlayer.getCurrentPosition()) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                            toMinutes((long) mPlayer.getCurrentPosition()))))
            );

            if (mPlayer.isPlaying()) {
                notification = new Runnable() {
                    public void run() {
                        primarySeekBarProgressUpdater();
                    }
                };
                mHandler.postDelayed(notification, 100);
            }

        } catch (Exception e) {

        }
    }


    @Override
    public void onStop() {
        super.onStop();
        Log.d(LOG_TAG, "OnStop method called");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(LOG_TAG, "OnStart method called");
    }


    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "Destroying Song Player Activity");
        mPlayer.pause();
        mPlayer.release();
//
//        mPlayer = null;
        super.onDestroy();
    }

    @TargetApi(21)
    public void showActionBarOnDialogFragment(View rootView) {
        // set the listener for Navigation
        Toolbar actionBar = (Toolbar) rootView.findViewById(R.id.df_action_bar);
        if (actionBar != null) {
            final SongPlayerActivityFragment window = this;
            actionBar.setTitle(R.string.app_name);
            actionBar.inflateMenu(R.menu.menu_main);
//                actionBar.setNavigationOnClickListener(new View.OnClickListener() {
//                    public void onClick(View v) {
//                        window.dismiss();
//                    }
//                });
        }
    }

    @TargetApi(21)
    public void hideActionBarOnDialogFragment(View rootView) {
        // set the listener for Navigation
        Toolbar actionBar = (Toolbar) rootView.findViewById(R.id.df_action_bar);
        if (actionBar != null) {
            actionBar.setVisibility(View.INVISIBLE);
        }
    }

    public void setTwoPane(boolean mTwoPane) {
        this.mTwoPane = mTwoPane;
    }
}
