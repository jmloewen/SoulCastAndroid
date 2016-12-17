package com.example.kevinzhang.soulpost;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private FirebaseRemoteConfig mFirebaseRemoteConfig;
//    MyInstanceIDListenerService myInstanceIDListenerService;
//    Device newdevice = new Device(-80085,80.085,0.8,myInstanceIDListenerService.deviceToken);
//    Soul newSoul = new Soul("Success:androidSoul","S3keyMissing", 1000000000, -666,66.6,0.6,myInstanceIDListenerService.deviceToken);
    Device newdevice = new Device(-1,8,0.8,"android token not available");
    Soul newSoul = new Soul("Success:androidSoul","S3keyMissing", 1000000000, -666,66.6,0.6,"android token not available");

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://soulcast.ml")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupFirebase();
//        devicePost();
//        soulPost();
//        deviceUpdate();
    }

    private void setupFirebase() {
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        //local var
        FirebaseRemoteConfigSettings firebaseRemoteConfigSettings =
                new FirebaseRemoteConfigSettings.Builder()
                        .setDeveloperModeEnabled(true)
                        .build();
        // Define default config values. Defaults are used when fetched config values are not
        // available. Eg: if an error occurred fetching values from the server.
        Map<String, Object> defaultConfigMap = new HashMap<>();
        defaultConfigMap.put("friendly_msg_length", 10L);
        // Apply config settings and default values.
        mFirebaseRemoteConfig.setConfigSettings(firebaseRemoteConfigSettings);
        mFirebaseRemoteConfig.setDefaults(defaultConfigMap);
    }

    private void deviceUpdate() {
        mockChange();
        // prepare call in Retrofit 2.0
        SoulpostAPI soulpostAPI = retrofit.create(SoulpostAPI.class);
        Call<Device> call = soulpostAPI.deviceUpdate(newdevice);
        call.enqueue(new Callback<Device>() {
            @Override
            public void onResponse(Call<Device> call, Response<Device> response) {
                //Log.d("SUCCESS SOUL POST", response.body().toString());
            }

            @Override
            public void onFailure(Call<Device> call, Throwable t) {
                //Log.d("FAIL SOUL POST", t.toString());
            }
        });
    }

    private void mockChange() {
        newdevice.longitude = -777.7;
        newdevice.latitude = 77.7;
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

    private void devicePost()  {
        // prepare call in Retrofit 2.0
        SoulpostAPI soulpostAPI = retrofit.create(SoulpostAPI.class);
        Call<Device> call = soulpostAPI.devicePost(newdevice);
        call.enqueue(new Callback<Device>() {
            @Override
            public void onResponse(Call<Device> call, Response<Device> response) {
                //Log.d("SUCCESS SOUL POST", response.body().toString());
            }

            @Override
            public void onFailure(Call<Device> call, Throwable t) {
                //Log.d("FAIL SOUL POST", t.toString());
            }
        });
    }


}
