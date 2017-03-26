package com.example.kevinzhang.soulpost;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyInstanceIDListenerService temp = new MyInstanceIDListenerService();

        Log.v("deviceToken: ",temp.getToken());
        Intent mapIntent = new Intent(this, MapActivity.class);
        startActivity(mapIntent);
    }

}
