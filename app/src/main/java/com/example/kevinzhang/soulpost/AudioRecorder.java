package com.example.kevinzhang.soulpost;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;

import java.io.File;
import java.io.IOException;

public class AudioRecorder {

    private static MediaRecorder mMediaRecorder;
    private static MediaPlayer mMediaPlayer;
    private static File mAudioFile;
    public boolean mHasAudioRecordingBeenStarted = false;
    private long recordingStartedTimeInMillis;
    private long recordingFinishedTimeInMillis;

    private AudioRecorderListener mAudioRecorderListener;

    public AudioRecorder(){
        mMediaRecorder =  new MediaRecorder();
        mMediaPlayer = new MediaPlayer();
        mAudioRecorderListener = null;
    }

    public void setmAudioRecorderListener(AudioRecorderListener audioRecorderListener){
        mAudioRecorderListener = audioRecorderListener;
    }

    public void startRecording(){
        try{
            recordingStartedTimeInMillis = System.currentTimeMillis();
            mAudioFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),
                    String.valueOf(recordingStartedTimeInMillis));
            setRecorder();
            mMediaRecorder.prepare();
            mMediaRecorder.start();
            mHasAudioRecordingBeenStarted = true;
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void stopRecording(){
        try {
            mMediaRecorder.stop();
            if (mAudioRecorderListener != null){
            }
            recordingFinishedTimeInMillis = System.currentTimeMillis();
        } catch (RuntimeException ex){

        }
        mMediaRecorder.reset();
        mHasAudioRecordingBeenStarted = false;
        long recordingTimeDifference = recordingFinishedTimeInMillis - recordingStartedTimeInMillis;
        if (recordingTimeDifference > 500){
            //start playing
            startPlaying();
        }
    }

    public void startPlaying(){
        try {
            mAudioRecorderListener.onRecordingFinished(mAudioFile);
            mMediaPlayer.setDataSource(mAudioFile.getAbsolutePath());
            mMediaPlayer.prepare();
            mMediaPlayer.start();
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

    private void setRecorder(){
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mMediaRecorder.setAudioSamplingRate(44100);
        mMediaRecorder.setAudioEncodingBitRate(96000);
        mMediaRecorder.setOutputFile(mAudioFile.getAbsolutePath());
    }

    public interface AudioRecorderListener{
        void onRecordingFinished(File audioFile);
    }
}

