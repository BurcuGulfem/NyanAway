package com.archsorceress.nyanaway;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    TextView timer;
    private long startTime = 0L;
    private Handler customHandler = new Handler();
    MediaPlayer mediaPlayer;
    GifDrawable nyangif;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mainview = inflater.inflate(R.layout.fragment_main, container, false);
        timer = (TextView) mainview.findViewById(R.id.timer);
        timer.setText("00:00:00:000");

        Button buttonStart = (Button) mainview.findViewById(R.id.button_start);
        Button buttonStop = (Button) mainview.findViewById(R.id.button_stop);

        buttonStart.setOnClickListener(timerStartListener);
        buttonStop.setOnClickListener(timerStopListener);

        mediaPlayer = MediaPlayer.create(this.getActivity(), R.raw.nyancat);
        mediaPlayer.setLooping(true);

        ImageView imageView = (ImageView) mainview.findViewById(R.id.imageView);
        try {
            nyangif = new GifDrawable( getResources(), R.raw.nyancat_transparent );
            imageView.setImageDrawable(nyangif);
        } catch (IOException e) {
            e.printStackTrace();
        }
        nyangif.stop();

        return mainview;
    }

    View.OnClickListener timerStartListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startTime = SystemClock.uptimeMillis();
            mediaPlayer.start();
            nyangif.start();
            customHandler.postDelayed(updateTimerThread, 0);
        }
    };

    View.OnClickListener timerStopListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mediaPlayer.pause();
            mediaPlayer.seekTo(0);
            nyangif.pause();
            customHandler.removeCallbacks(updateTimerThread);
        }
    };

    private Runnable updateTimerThread = new Runnable() {

        public void run() {

            long timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            int milliseconds = (int) (timeInMilliseconds % 1000);
            int seconds = (int) (timeInMilliseconds / 1000) % 60;
            int minutes = (int) ((timeInMilliseconds / (1000 * 60)) % 60);
            int hours = (int) ((timeInMilliseconds / (1000 * 60 * 60)) % 24);
            timer.setText(String.format("%02d:%02d:%02d:%03d", hours, minutes, seconds, milliseconds));
            customHandler.postDelayed(this, 0);
        }

    };

    @Override
    public void onStop() {
        super.onStop();
        nyangif.stop();
        nyangif.recycle();
        mediaPlayer.stop();
        mediaPlayer.release();
    }
}
