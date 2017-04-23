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
 * Created by Jonathan on 3/26/2017.
 */

public class PermissionsManager {
    //permission request codes
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1 ;
    private final int AUDIO_PERMISSION_REQUEST_CODE = 2;
    private final int STORAGE_PERMISSION_REQUEST_CODE = 3;

    private final int LOCATION_AUDIO_STORAGE_PERMISSION_REQUEST_CODE = 1;
    private Context callerContext;


    public PermissionsManager(final Context context) {
        callerContext = context;
    }

    public void getAllPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            ActivityCompat.requestPermissions((Activity) callerContext,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                 Manifest.permission.RECORD_AUDIO,
                                 Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    LOCATION_AUDIO_STORAGE_PERMISSION_REQUEST_CODE);
//            getLocationPermission();
//            getAudioPermission();
//            getStoragePermission();
        }

    }

    private void getStoragePermission() {
        ActivityCompat.requestPermissions((Activity) callerContext,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                STORAGE_PERMISSION_REQUEST_CODE);

    }

    private void getAudioPermission() {
        ActivityCompat.requestPermissions((Activity) callerContext,
                new String[]{Manifest.permission.RECORD_AUDIO},
                AUDIO_PERMISSION_REQUEST_CODE);

    }

    private void getLocationPermission() {
        ActivityCompat.requestPermissions((Activity) callerContext,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE);
    }

    public boolean hasAllPermissions() {
        boolean hasAllPermissions = false;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        if (hasLocationPermission() && hasAudioPermission() && hasStoragePermission()){

            hasAllPermissions = true;
        }

        return  hasAllPermissions;
    }

    private boolean hasStoragePermission() {
        return (ContextCompat.checkSelfPermission(callerContext, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED);
    }

    private boolean hasAudioPermission() {
        return false;
    }

    private boolean hasLocationPermission() {
        return true;
    }



}
