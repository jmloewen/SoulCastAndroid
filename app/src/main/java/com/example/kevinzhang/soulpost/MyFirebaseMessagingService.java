package com.example.kevinzhang.soulpost;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.google.firebase.auth.api.model.StringList;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.File;

/**
 * Created by kevinzhang on 2016-12-17.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFMService";
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private static final String SOULPREFS = "SoulcastPreferences";
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


    }

    private void beginDownload(final String S3key){
        Log.d(TAG, "Download has begun");

        TransferUtility mTransferUtility;
        mTransferUtility = Util.getTransferUtility(this);
        File audioFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),
                String.valueOf(S3key));

        TransferObserver observer = mTransferUtility.download(Constants.BUCKET_NAME, S3key, audioFile);

    }
}
