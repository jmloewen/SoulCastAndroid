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
        permissionsMan = new PermissionsManager();
        if (permissionsMan.hasAllPermissions() == true) {
            Intent mapIntent = new Intent(this, MapActivity.class);
            startActivity(mapIntent);
        }else{
            //upon completion of getting all permissions, start map activity
            permissionsMan.getAllPermissions();
        }

//        Intent mapIntent = new Intent(this, MapActivity.class);
//        startActivity(mapIntent);
    }

}
