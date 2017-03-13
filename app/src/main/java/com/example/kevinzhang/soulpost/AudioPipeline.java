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
    private String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+String.valueOf(recordingStartedTimeInMillis);
    File mAudioFile = new File(filePath);
    MyFileObserver fb = new MyFileObserver(filePath, FileObserver.CLOSE_WRITE);
    private AudioRecorder mMediaRecorder = new AudioRecorder(filePath, mAudioFile);
    AudioPlayer mAudioPlayer = new AudioPlayer();

    public void startRecording(){
        mMediaRecorder.startRecording();
    }

    public void stopRecording(){
        mMediaRecorder.stopRecording();
    }

    class MyFileObserver extends FileObserver {

        public MyFileObserver (String path, int mask) {
            super(path, mask);
        }

        public void onEvent(int event, String path) {

            mAudioPlayer.startPlaying(mAudioFile);
        }
    }

}
