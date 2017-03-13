package com.example.kevinzhang.soulpost;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //This is where we want to open the map fragment in SoulCast-Proto.
        Intent mapIntent = new Intent(this, MapActivity.class);
        startActivity(mapIntent);

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
}
