package com.example.kevinzhang.soulpost;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;

public class AudioPlayer{

    private static MediaPlayer mMediaPlayer;

    public AudioPlayer(){
        mMediaPlayer = new MediaPlayer();
    }

    public void startPlaying(String filePath){


        try {

            mMediaPlayer.setDataSource(filePath);
            Log.d("AudioPlayer","start AudioPlayer");
            mMediaPlayer.prepare();
            mMediaPlayer.start();
            Log.d("AudioPlayer","stop AudioPlayer");
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mMediaPlayer.reset();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}