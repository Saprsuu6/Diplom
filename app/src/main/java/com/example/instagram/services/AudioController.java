package com.example.instagram.services;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.instagram.R;

public class AudioController {
    private final MediaPlayer mediaPlayer;
    private final SeekBar seekBar;
    private Handler handler;
    private final ImageView playStop;
    private final ImageView playPrev;
    private final ImageView playNext;
    private final TextView timeLine;
    private final Context context;
    private UpdateSeekBar updateSeekBar;

    public AudioController(TextView timeLine, SeekBar seekBar, ImageView playStop, ImageView playPrev, ImageView playNext, Context context, Uri uri) {
        this.timeLine = timeLine;
        this.context = context;
        this.playStop = playStop;
        this.playPrev = playPrev;
        this.playNext = playNext;
        this.seekBar = seekBar;

        mediaPlayer = MediaPlayer.create(context, uri);
        seekBar.setMax(mediaPlayer.getDuration());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.d("onStartTrackingTouch: ", seekBar.toString());
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d("onStopTrackingTouch: ", seekBar.toString());
            }
        });

        setListeners();
    }

    @SuppressLint("UseCompatLoadingForDrawables")

    private void setListeners() {
        playStop.setOnClickListener(v -> {
            if (mediaPlayer != null) {
                if (!mediaPlayer.isPlaying()) {
                    playAudio();
                    playStop.setImageDrawable(context.getDrawable(R.drawable.stop));
                } else {
                    pauseAudio();
                    playStop.setImageDrawable(context.getDrawable(R.drawable.play));
                }
            }
        });

        playPrev.setOnClickListener(v -> {
            if (mediaPlayer != null) {
                deRewind();
            }
        });

        playNext.setOnClickListener(v -> {
            if (mediaPlayer != null) {
                doNext();
            }
        });
    }

    private void deRewind() {
        if (mediaPlayer != null) {
            int SUB_TIME = 5000;
            int curPosition = mediaPlayer.getCurrentPosition();
            if (curPosition - SUB_TIME > 0) {
                mediaPlayer.seekTo(curPosition - SUB_TIME);
            }
        }
    }

    private void doNext() {
        if (mediaPlayer != null) {
            int ADD_TIME = 5000;
            int curPosition = mediaPlayer.getCurrentPosition();
            int duration = mediaPlayer.getDuration();
            if (curPosition + ADD_TIME < duration) {
                mediaPlayer.seekTo(curPosition + ADD_TIME);
            }
        }
    }

    private void playAudio() {
        mediaPlayer.start();
        updateSeekBar = new UpdateSeekBar();
        handler.post(updateSeekBar);
    }

    private void pauseAudio() {
        mediaPlayer.pause();
    }

    public void clearHandler() {
        if (handler != null) {
            pauseAudio();
            handler.removeCallbacks(updateSeekBar);
        }
    }

    public void initHandler(Handler newHandler) {
        if (handler == null) {
            handler = newHandler;
        }
    }

    public int getDuration() {
        if (mediaPlayer != null) {
            return mediaPlayer.getDuration();
        }
        return 0;
    }

    public class UpdateSeekBar implements Runnable {
        @SuppressLint("SetTextI18n")
        @Override
        public void run() {
            int delay = 100;
            seekBar.setProgress(mediaPlayer.getCurrentPosition());
            timeLine.setText(GFG.convert(mediaPlayer.getCurrentPosition()) + " / " + GFG.convert(mediaPlayer.getDuration()));
            handler.postDelayed(this, delay);
        }
    }
}
