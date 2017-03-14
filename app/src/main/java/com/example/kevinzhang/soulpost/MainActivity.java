package com.example.kevinzhang.soulpost;

import android.*;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    //fake data registration - suppose user's location services are turned off.
    Device newdevice;



    Soul newSoul;
//    Device newdevice = new Device(1,8, (float) 0.8,"android headers added");
//    Soul newSoul = new Soul("Success:androidSoul","S3keyMissing", 1000000000, -666,66.6,0.6,"android token not available");

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://soulcast.ml")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    private final int AUDIO_AND_STORAGE_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // must get audio and storage permission before mapActivity since record button is initialized in mapActivity's onCreate()
        ActivityCompat.requestPermissions(this, new String[]{
                android.Manifest.permission.RECORD_AUDIO,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE},
                AUDIO_AND_STORAGE_PERMISSION_REQUEST_CODE);


        //This is where we want to open the map fragment in SoulCast-Proto.
       // Intent mapIntent = new Intent(this, MapActivity.class);
        //startActivity(mapIntent);

        //soulPost();

        //this should be moved to mapactivity
//        getNearby();
    }

    private void getNearby() {
        SoulpostAPI soulpostAPI = retrofit.create(SoulpostAPI.class);
        Call<Nearby> call = soulpostAPI.getNearby(newdevice.getId());
        call.enqueue(new Callback<Nearby>() {
            @Override
            public void onResponse(Call<Nearby> call, Response<Nearby> response) {
                if (response.isSuccessful()){
                    Log.d("Devices nearby:", response.body().devicesNearby + "");
                }else{
                    Log.d("Resp not success:", response.body().devicesNearby + "");
                }

            }

            @Override
            public void onFailure(Call<Nearby> call, Throwable t) {
                //TODO Malform json
                Log.d("Nearby call failed:", t.toString());
            }
        });
    }

    private void mockChange() {
        newdevice.setId(15);
        newdevice.setLongitude(-787.7f);
        newdevice.setLatitude(78.7f);
    }

    private void soulPost() {
        // prepare call in Retrofit 2.0
        SoulpostAPI soulpostAPI = retrofit.create(SoulpostAPI.class);
        Call<Soul> call = soulpostAPI.soulPost(newSoul);
        call.enqueue(new Callback<Soul>() {
            @Override
            public void onResponse(Call<Soul> call, Response<Soul> response) {
                Log.d("onSUCCESS", "Success SoulPost");
            }

            @Override
            public void onFailure(Call<Soul> call, Throwable t) {
                Log.d("onFail", "Fail SoulPost");
            }
        });
    }

    private void checkAudio_Storage_Permissions(){
        checkPermission(android.Manifest.permission.RECORD_AUDIO, AUDIO_AND_STORAGE_PERMISSION_REQUEST_CODE);
    }

    private void checkStoragePermission(){
        checkPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, AUDIO_AND_STORAGE_PERMISSION_REQUEST_CODE);
    }

    private void checkPermission(String permissionType, int requestCode){
        if (ContextCompat.checkSelfPermission(this, permissionType) != PackageManager.PERMISSION_GRANTED){
            requestPermission(permissionType, requestCode);
        }
    }
    private void requestPermission(String permissionType, int requestCode){
        ActivityCompat.requestPermissions(this, new String[]{permissionType}, requestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if(requestCode == 1){
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // permission was granted
                Log.v("Audio_storage_perm","granted");
                Intent mapIntent = new Intent(this, MapActivity.class);
                startActivity(mapIntent);


            } else {

                // Permission denied, Disable the functionality that depends on this permission.
                Toast.makeText(this, "Audio and Storage permissions denied", Toast.LENGTH_LONG).show();
               //finish(); //exit the app

            }

            // other cases to check for other permissions this app might request.
        }
    }
}
