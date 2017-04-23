package com.example.kevinzhang.soulpost;

import android.*;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;

/**
 * Created by Jonathan on 3/26/2017.
 */

public class PermissionsManager {
    //permission request codes
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1 ;
    private final int AUDIO_PERMISSION_REQUEST_CODE = 2;
    private final int STORAGE_PERMISSION_REQUEST_CODE = 3;
    private Context callerContext;


    public PermissionsManager(final Context context) {
        callerContext = context;
    }

    public static void getAllPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            checkLocationPermission();
        }

    }

    public boolean hasAllPermissions() {
        boolean hasAllPermissions = false;

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
        return false;
    }


}
