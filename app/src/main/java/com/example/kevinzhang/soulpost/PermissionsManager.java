package com.example.kevinzhang.soulpost;

import android.os.Build;

/**
 * Created by Jonathan on 3/26/2017.
 */

public class PermissionsManager {
    //bools

    public PermissionsManager() {
        //init bools
    }

    public static void getAllPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            checkLocationPermission();
        }

    }

    public static boolean hasAllPermissions() {
        boolean hasAllPermissions = false;
        return  hasAllPermissions;
    }

    private static class PermissionGetter {
        public void getLocationPerm(){

        }
        public void getStoragePerm(){

        }
        public void getMediaPerm(){

        }

        public void hasLocationPerm(){

        }
        public void hasStoragePerm(){

        }
        public void hasMediaPerm(){

        }

        public void gotLocationPerm(){

        }
        public void gotStoragePerm(){

        }
        public void gotMediaPerm(){

        }
    }

}
