package com.example.kevinzhang.soulpost;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import static java.security.AccessController.getContext;

/**
 * Created by kai on 2017-03-12.
 */

public class AudioPipeline {
    private static final String TAG = "AudioPipeline";

    private static MediaRecorder mMediaRecorder;
    private static MediaPlayer mMediaPlayer;
    private static File mAudioFile;
    public boolean mHasAudioRecordingBeenStarted = false;
    private long recordingStartedTimeInMillis;
    private long recordingFinishedTimeInMillis;

    private AudioPipeline.AudioPipelineListener mAudioPipelineListener;

    public AudioPipeline() {
        mMediaRecorder =  new MediaRecorder();
        mMediaPlayer = new MediaPlayer();
        mAudioPipelineListener = null;
    }

    public void setmAudioPipelineListener(AudioPipeline.AudioPipelineListener audioPipelineListener){
        mAudioPipelineListener = audioPipelineListener;
    }

    public void startRecording(){
        try{
            setNewFile();
            setRecorder();
            mMediaRecorder.prepare();
            mMediaRecorder.start();
            mHasAudioRecordingBeenStarted = true;
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private void setNewFile() {
        recordingStartedTimeInMillis = System.currentTimeMillis();
        mAudioFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),
                String.valueOf(recordingStartedTimeInMillis) + ".mp3");
    }

    public void stopRecording(){
        try {
            mMediaRecorder.stop();
            recordingFinishedTimeInMillis = System.currentTimeMillis();
        } catch (RuntimeException ex){
            Log.d(TAG, "mMediaRecorder stop recording Runtimeexception");
        }
        mMediaRecorder.reset();
        mHasAudioRecordingBeenStarted = false;
        long recordingTimeDifference = recordingFinishedTimeInMillis - recordingStartedTimeInMillis;

        if (recordingTimeDifference > 500){
            startPlaying();
            Log.d(TAG, "Audiopipeline startPlaying() called from stopRecording");
        }else {
//            Toast.makeText(getContext(), "Soul too short", Toast.LENGTH_LONG).show();
        }
    }

    public void startPlaying(){
        try {
            mAudioPipelineListener.onRecordingFinished(mAudioFile);
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

    public interface AudioPipelineListener{
        void onRecordingFinished(File audioFile);
    }
}
