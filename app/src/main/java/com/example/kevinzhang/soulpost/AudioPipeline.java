package com.example.kevinzhang.soulpost;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.FileObserver;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * Created by kai on 2017-03-12.
 */

public class AudioPipeline {


    public boolean mHasAudioRecordingBeenStarted = false;
    private long recordingStartedTimeInMillis = System.currentTimeMillis();
    private long recordingFinishedTimeInMillis;
    private String filePath;
    File mAudioFile;
    private AudioRecorder mMediaRecorder;
    AudioPlayer mAudioPlayer;

    public AudioPipeline(){
         filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+String.valueOf(recordingStartedTimeInMillis);
       //  mAudioFile = new File(filePath);
         mMediaRecorder = new AudioRecorder();
         mAudioPlayer = new AudioPlayer();
    }

    public void startRecording(){
        filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+String.valueOf(recordingStartedTimeInMillis);
        mMediaRecorder.startRecording(filePath);
    }

    public void stopRecording(){

        mMediaRecorder.stopRecording();
        mAudioPlayer.startPlaying(filePath);
    }

}
