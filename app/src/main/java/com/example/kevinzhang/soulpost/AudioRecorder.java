package com.example.kevinzhang.soulpost;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.FileObserver;
import android.util.Log;

import java.io.File;
import java.io.IOException;

public class AudioRecorder {

    private static MediaRecorder mMediaRecorder;
    public boolean mHasAudioRecordingBeenStarted = false;
    private long recordingStartedTimeInMillis;
    private long recordingFinishedTimeInMillis;
    private String filePath;
    File mAudioFile;
    //private AudioRecorderListener mAudioRecorderListener;


    public AudioRecorder(){
        mMediaRecorder =  new MediaRecorder();
}

    /*public void setmAudioRecorderListener(AudioRecorderListener audioRecorderListener){
       // mAudioRecorderListener = audioRecorderListener;
    }*/

    public void startRecording(String FileP){
        try{
            setRecorder(FileP);
            mMediaRecorder.prepare();
            mMediaRecorder.start();
            mHasAudioRecordingBeenStarted = true;
        } catch (IOException e){
            Log.v("Start Recording","Start Recording Error");
            e.printStackTrace();
        }
    }

    public void stopRecording(){
        try {
            mMediaRecorder.stop();
            Log.d("ARSTPREC", "STOP RECORD 1");

            recordingFinishedTimeInMillis = System.currentTimeMillis();
            Log.d("ARSTPREC", "STOP RECORD 2");

        } catch (RuntimeException ex){
            Log.d("ARSTPREC", "STOP RECORD CATCH");

        }
        mMediaRecorder.reset();
        Log.d("ARSTPREC", "STOP RECORD 3");

        mHasAudioRecordingBeenStarted = false;

        long recordingTimeDifference = recordingFinishedTimeInMillis - recordingStartedTimeInMillis;
        Log.d("ARSTPREC", "STOP RECORD 4");

        if (recordingTimeDifference > 500){
            //start playing
            Log.d("ARSTPREC", "STOP RECORD 5");

            Log.d("ARSTPREC", "STOP RECORD 6");
        }
    }




    private void setRecorder(String fileP){
        filePath = fileP;
        mAudioFile = new File(filePath);
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mMediaRecorder.setAudioSamplingRate(44100);
        mMediaRecorder.setAudioEncodingBitRate(96000);
        mMediaRecorder.setOutputFile(filePath);
    }


}

