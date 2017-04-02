package com.example.kevinzhang.soulpost;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

/**
 * Created by kevinzhang on 2016-12-17.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFMService";

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private static final String SOULPREFS = "SoulcastPreferences";
    private static MediaPlayer mMediaPlayer;
    private static File mAudioFile;


    /**
     * This is where we get a push notification from the server.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

      //  Map<String,String> data = remoteMessage.getData();
       Log.v("myToken","msgreceived");
        printFCMMessage(remoteMessage);
        savePrefs(remoteMessage);
        beginDownload(prefs.getString("PushS3Key", "NO KEY STORED"));

    }

    private void printFCMMessage(RemoteMessage remoteMessage) {
//         Handle data payload of FCM messages.
         Log.d(TAG, "FCM Message Id: " + remoteMessage.getMessageId());
         Log.d(TAG, "FCM Notification Message: " +
               remoteMessage.getNotification());
               Log.d(TAG, "FCM Data Message: " + remoteMessage.getData());
    }

    private void savePrefs(RemoteMessage remoteMessage) {

        prefs = getSharedPreferences(SOULPREFS, Context.MODE_PRIVATE);
        editor = prefs.edit();
        Map<String,String> data = remoteMessage.getData();
        editor.putString("PushS3Key",data.get("S3key"));
        Log.v("S3Key_is: ",data.get("S3key"));
        //editor.putString("PushS3Key", remoteMessage.getData().get("S3key"));
        editor.commit();
        Log.d(TAG, "S3Key: " + prefs.getString("PushS3Key", "NO KEY STORED"));
        sendNotification(prefs.getString("PushS3Key", "NO KEY STORED"));

    }

    private void playSoul(final String S3key) {
        Log.d(TAG, "Begin playing soul");

        mMediaPlayer = new MediaPlayer();
        mAudioFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),
                String.valueOf(S3key));

        try {
            FileInputStream fd = openFile(mAudioFile);
            mMediaPlayer.setDataSource(fd.getFD());
//            Log.d(TAG, "Path: " + mAudioFile.getAbsolutePath());
            mMediaPlayer.prepare();
            mMediaPlayer.start();
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mMediaPlayer.reset();
                    Log.d(TAG, "Finished playing soul. reset mediaPlayer");
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

    public void beginDownload(final String S3key){
        Log.d(TAG, "Download has begun");

        TransferUtility mTransferUtility = Util.getTransferUtility(this);
        File audioFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),
                String.valueOf(S3key));

        TransferObserver observer = mTransferUtility.download(Constants.BUCKET_NAME, S3key, audioFile);

        observer.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState newState) {
                switch (newState) {
                    case COMPLETED:
                        Log.v("transferListener", " download completed");
                        //playSoul(S3key);
                }
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

    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra("S3key",messageBody);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_ic_notification)
                .setContentTitle("FCM Message")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Log.v("sendNotification","finished building notification");
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

}
