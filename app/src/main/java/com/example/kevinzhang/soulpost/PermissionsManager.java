package com.example.kevinzhang.soulpost;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * PermissionsManager handles permissions requests for the application.
 * If we ever need more permissions than we have right now, it might be advisable to throw them all into an array instead of hardcoding them.
 */

public class PermissionsManager {
    //permission request codes
    private final int LOCATION_PERMISSION_REQUEST_CODE = 1 ;
    private final int AUDIO_PERMISSION_REQUEST_CODE = 2;
    private final int STORAGE_PERMISSION_REQUEST_CODE = 3;

    //expected results of the above codes.
    private final int LOCATION_AUDIO_STORAGE_PERMISSION_REQUEST_CODE = 1;
    private Context callerContext;


    /**
     * Our constructor sets the context as passed from MainActivity.
     * @param context
     */
    public PermissionsManager(final Context context) {
        callerContext = context;
    }

    /**
     * Here we request all of the permissions we need for the application.
     */
    public void getAllPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            ActivityCompat.requestPermissions((Activity) callerContext,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                 Manifest.permission.RECORD_AUDIO,
                                 Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    LOCATION_AUDIO_STORAGE_PERMISSION_REQUEST_CODE);

        }

    }

    /**
     * A getter for the state of our permission requests
     * @return  whether or not we have all permissions.
     */
    public boolean hasAllPermissions() {

        //if the build version is sufficiently low that we don't need to request permissions, return true.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        if (hasLocationPermission() && hasAudioPermission() && hasStoragePermission()){
            return true;
        }
        return false;
    }

    /**
     * Check status of Storage permission.
     * @return
     */
    private boolean hasStoragePermission() {
        return (ContextCompat.checkSelfPermission(callerContext, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED);
    }

    /**
     * Check status of Audio permission
     * @return
     */
    private boolean hasAudioPermission() {
        return (ContextCompat.checkSelfPermission(callerContext, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED);
    }

    /**
     * Check status of Location permission
     * @return
     */
    private boolean hasLocationPermission() {
        return (ContextCompat.checkSelfPermission(callerContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED);
    }
}
