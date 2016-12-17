package com.example.kevinzhang.soulpost;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.PATCH;
import retrofit2.http.POST;

/**
 * Created by kevinzhang on 2016-12-10.
 */

public interface SoulpostAPI {
    @POST("/devices")
    Call<Device> devicePost(@Body Device device);
    @POST("/souls")
    Call<Soul> soulPost(@Body Soul soul);
    @PATCH("/devices/{id}")
    Call<Device> deviceUpdate(@Body Device device);
}
