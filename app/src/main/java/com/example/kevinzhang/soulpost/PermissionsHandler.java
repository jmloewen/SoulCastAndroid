package com.example.kevinzhang.soulpost;

import android.*;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.content.Context;
import android.app.Activity;
import android.widget.Toast;

import com.google.android.gms.drive.Permission;

/**
 * Created by Jonathan on 3/26/2017.
 */

public class PermissionsHandler {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private final int AUDIO_AND_STORAGE_PERMISSION_REQUEST_CODE = 2;
    private Context context;
    private Activity activity;

    public PermissionsHandler(){

    }

    public void locationPermissionCheck(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
    }

    private void checkLocationPermission() {
        if (!fineLocationGranted()) {
            requestFineLocationPermissions();
        }
    }

    public void requestFineLocationPermissions() {
        ActivityCompat.requestPermissions(activity,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE);
    }

    public boolean fineLocationGranted() {
        return (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED);
    }


    public void checkAudioAndStoragePermission() {
        if (!audioGranted() && !storageGranted()) {
            requestAudioAndStoragePermissions();
        }
    }

    private boolean audioGranted() {
        return (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED);
    }

    private boolean storageGranted() {
        return (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED);
    }

    public void requestAudioAndStoragePermissions() {
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                AUDIO_AND_STORAGE_PERMISSION_REQUEST_CODE);
    }
}
