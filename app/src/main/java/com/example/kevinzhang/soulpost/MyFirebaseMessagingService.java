package com.example.kevinzhang.soulpost;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.google.android.gms.games.GamesMetadata;
import com.google.firebase.auth.api.model.StringList;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * Created by kevinzhang on 2016-12-17.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFMService";
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private static final String SOULPREFS = "SoulcastPreferences";

    //custom variables to make soulcast work
    private static MediaPlayer mMediaPlayer;
    private static File mAudioFile;



    @Override
    /**
     * This is where we get a push notification from the server.
     */
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Handle data payload of FCM messages.
        Log.d(TAG, "FCM Message Id: " + remoteMessage.getMessageId());
        Log.d(TAG, "FCM Notification Message: " +
                remoteMessage.getNotification());
        Log.d(TAG, "FCM Data Message: " + remoteMessage.getData());
        prefs = getSharedPreferences(SOULPREFS, Context.MODE_PRIVATE);
        editor = prefs.edit();

        //this is the S3 key of the message pushed from the server.
        editor.putString("PushS3Key", remoteMessage.getData().get("S3key"));
        editor.commit();
        Log.d(TAG, "S3Key: " + prefs.getString("PushS3Key", "NO KEY STORED"));
        beginDownload(prefs.getString("PushS3Key", "NO KEY STORED"));
       // playSoul(prefs.getString("PushS3Key", "NO KEY STORED"));

    }

    private void playSoul(final String S3key) {
        Log.d(TAG, "Begin playing soul");
        mMediaPlayer = new MediaPlayer();

        mAudioFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),
                String.valueOf(S3key));
        try {
            Log.d(TAG, "Path: " + mAudioFile.getAbsolutePath());

            Log.d(TAG, "Begin playing soul1");

            FileInputStream fd = openFile(mAudioFile);
            mMediaPlayer.setDataSource(fd.getFD());
            Log.d(TAG, "Path: " + mAudioFile.getAbsolutePath());
            mMediaPlayer.prepare();
            Log.d(TAG, "Begin playing soul2");
            mMediaPlayer.start();
            Log.d(TAG, "Begin playing soul3");
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mMediaPlayer.reset();
                    Log.d(TAG, "Finished playing soul");
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, e.getMessage());
            Log.d(TAG, e.toString());
        }
    }

    private FileInputStream openFile(File file) throws FileNotFoundException, IOException {
        FileInputStream fos = new FileInputStream(file);
        // remember th 'fos' reference somewhere for later closing it
        return fos;
    }

    private void beginDownload(final String S3key){
        Log.d(TAG, "Download has begun");

        TransferUtility mTransferUtility;
        mTransferUtility = Util.getTransferUtility(this);
        File audioFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),
                String.valueOf(S3key));

        TransferObserver observer = mTransferUtility.download(Constants.BUCKET_NAME, S3key, audioFile);

        observer.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState newState) {
                //Enum status = newState.valueOf("Completed");
                Log.v("transferListener","In OnstateChanged");
                switch (newState) {
                    case COMPLETED:
                        Log.v("transferListener", " download completed");
                        playSoul(S3key);
                }
                Log.v("transfer listener", "here");
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                String str = Long.toString(bytesCurrent);
                Log.v("transfer listener", str);
            }

            @Override
            public void onError(int id, Exception e) {
                e.printStackTrace();
            }
        });
    }
}
