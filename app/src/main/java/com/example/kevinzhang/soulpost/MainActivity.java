package com.example.kevinzhang.soulpost;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    Device device = new Device(123,456,7,"post1");

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://soulcast.ml")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        soulPost();
    }

    private void soulPost()  {
        // prepare call in Retrofit 2.0
        SoulpostAPI soulpostAPI = retrofit.create(SoulpostAPI.class);
        Call<Device> call = soulpostAPI.devicePost(device);
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
