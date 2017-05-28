package com.camvy.kevinzhang.soulcast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by kevinzhang on 2016-12-10.
 */

public interface SoulpostAPI {
    @Headers("Accept: application/json")
    @POST("/devices")
    Call<Device> devicePost(@Body Device device);

    @Headers("Accept: application/json")
    @GET("/device_history/1")
    Call<ArrayList<Soul>> getHistory();
    //Call<Device> getHistory(@Path("id") int id);

    @Headers("Accept: application/json")
    @POST("/souls")
    Call<Soul> soulPost(@Body Soul soul);

    @Headers("Accept: application/json")
    @POST("/echo")
    Call<Soul> echo(@Body Soul soul);

    @Headers("Accept: application/json")
    @PATCH("/devices/{id}")
    Call<Device> deviceUpdate(@Body Device device, @Path("id") int id);

    @Headers("Accept: application/json")
    @GET("/nearby/{id}")
    Call<Nearby> getNearby(@Path("id") int  id);

}
