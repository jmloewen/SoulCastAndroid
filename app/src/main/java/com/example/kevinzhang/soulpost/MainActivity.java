package com.example.kevinzhang.soulpost;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

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

            //upon completion of getting all permissions, start map activity
            permissionsMan.getAllPermissions();
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults){
        //here'
        Log.d("Test", "Test");

    }


}
