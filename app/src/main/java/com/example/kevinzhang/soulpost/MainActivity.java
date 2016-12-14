package com.example.kevinzhang.soulpost;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    Device newdevice = new Device(-666,66.6,0.6,"androidTokenTestDec14");
    Soul newSoul = new Soul("androidSoul","testkey", 1000000000, -666,66.6,0.6,"androidTokenTestDec14");

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://soulcast.ml")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        devicePost();
//        soulPost();
        deviceUpdate();
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
