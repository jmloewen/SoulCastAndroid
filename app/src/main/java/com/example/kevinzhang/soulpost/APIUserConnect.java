package com.example.kevinzhang.soulpost;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Jonathan on 2/5/2017.
 */

public class APIUserConnect {

    /***
     * This is for first time user connection, creates a registration with a location-device id tuple.
     */
    public static Device RegisterDevice(LatLng latLng, final Context context){
        //get location
        //get device token
        //create the tuple - device and location
        //send to the server.
        //server responds, parse response.
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://soulcast.ml")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // prepare call in Retrofit 2.0
        SoulpostAPI soulpostAPI = retrofit.create(SoulpostAPI.class);
        final Device newdevice;
        newdevice = new Device((float)latLng.latitude,(float)latLng.longitude,(float)0.03, FirebaseInstanceId.getInstance().getToken());
        Log.d("Token", FirebaseInstanceId.getInstance().getToken());
        Call<Device> call = soulpostAPI.devicePost(newdevice);
        call.enqueue(new Callback<Device>() {
            @Override
            public void onResponse(Call<Device> call, Response<Device> response) {
                if (response.isSuccessful()){
                    //Log.d("Server response success", new Gson().toJson(response));
                    Log.d("ID is :",response.body().id + "");
                    newdevice.id = response.body().id;
                }else {
                    //some kind of server error
                    Log.d("Server response error",new Gson().toJson(response));
                    Log.d("ERIC Server error :",response.body() + "");
                }

            }

            @Override
            public void onFailure(Call<Device> call, Throwable t) {
                Toast.makeText(context, "CNXNFLREGDVCEs", Toast.LENGTH_SHORT).show();
            }
        });

        return newdevice;
    }

    /***
     * This is for updating user geolocation, minute to minute.
     */
    public static void UpdateDevice(Device userDevice, final Context context){
        //this simply updates the location, using the ID from the server.

        //get location
        //get ID
        //send ID and location to server

        //server responds with status 200

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://soulcast.ml")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Log.d("Update", "Update Function");
        SoulpostAPI soulpostAPI = retrofit.create(SoulpostAPI.class);
        Call<Device> call = soulpostAPI.deviceUpdate(userDevice, userDevice.id);
        call.enqueue(new Callback<Device>() {
            @Override
            public void onResponse(Call<Device> call, Response<Device> response) {
                if (response.isSuccessful()){
                    //Log.d("Server response success", new Gson().toJson(response));
                    Log.d("UPDSUCC", response.message() + "|||" + response.body() + "|||" + response.code());
                    Log.d("ID is :",response.body().id + "");
                }else {
                    //some kind of server error
                    //Log.d("Server response error",new Gson().toJson(response));
                    Log.d("Server error :",response.body() + "");
                }
            }

            @Override
            public void onFailure(Call<Device> call, Throwable t) {
                Toast.makeText(context, "UpdRespFail", Toast.LENGTH_SHORT).show();

            }
        });
    }

    public static void GetNearby(float userRadius){
        //This gets souls nearby the user.
        //This takes in the value userRadius, and asks the server how many other devices are nearby.
            //(this will eventually check if they are active or not)
        //sends to the server userRadius, gets back a number in JSON
    }


}
