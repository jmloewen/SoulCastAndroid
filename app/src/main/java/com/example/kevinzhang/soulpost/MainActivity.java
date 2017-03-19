package com.example.kevinzhang.soulpost;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent mapIntent = new Intent(this, MapActivity.class);
        startActivity(mapIntent);

    }

}
