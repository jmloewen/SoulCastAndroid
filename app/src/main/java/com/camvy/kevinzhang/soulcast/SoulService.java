package com.camvy.kevinzhang.soulcast;

import android.util.Log;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by kai on 2017-03-08.
 */

public class SoulService {
    Retrofit retrofit;
    Device newdevice;
    Soul newSoul;

    public SoulService() {
        retrofit = new Retrofit.Builder()
                .baseUrl("http://soulcast.ml")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public SoulpostAPI createSoulpostAPI(){
        return retrofit.create(SoulpostAPI.class);
    }

    public void getNearby() {
        SoulpostAPI soulpostAPI = createSoulpostAPI();
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

    public void soulPost() {
        // prepare call in Retrofit 2.0
        SoulpostAPI soulpostAPI = createSoulpostAPI();
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
