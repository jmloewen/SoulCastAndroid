package com.example.kevinzhang.soulpost;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    PermissionsManager permissionsMan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        permissionsMan = new PermissionsManager(this);
        if (permissionsMan.hasAllPermissions()) {
            Intent mapIntent = new Intent(this, MapActivity.class);
            startActivity(mapIntent);
        }else{
            permissionsMan.getAllPermissions();
        }

    }

    /**
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     * success is grantresults = 0, failure is -1.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults){

        printPermissionResults(requestCode, permissions, grantResults);

        if (grantResults.length == 3){
            Log.d("grResArray", "length is 3");
            if (grantResults[0]==0 && grantResults[1]==0 && grantResults[2]==0){
                Intent mapIntent = new Intent(this, MapActivity.class);
                startActivity(mapIntent);
            }
        }

    }

    private void printPermissionResults(int requestCode, String[] permissions, int[] grantResults) {
        Log.d("oRPR", "enter onrequestpermissionsresult");
        Log.d("reqCode", "" + requestCode);
        for (int i = 0; i < permissions.length; i++)
        {
            Log.d("permArray", permissions[i]);
        }
        for (int i = 0; i < grantResults.length; i++)
        {
            Log.d("grResArray", "" + grantResults[i]);
        }

    }


}
