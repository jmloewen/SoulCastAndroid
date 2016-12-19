package com.example.kevinzhang.soulpost;

import com.google.gson.annotations.SerializedName;

/**
 * Created by kevinzhang on 2016-12-10.
 */

public class Device {
    @SerializedName("longitude") public float longitude;
    @SerializedName("latitude") public float latitude;
    @SerializedName("radius") public float radius;
    @SerializedName("token") public String token;
    public int id;

    public Device(float longitude, float latitude, float radius, String token) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.radius = radius;
        this.token = token;
    }

//    public void setID(int idFromServer){
//        id = idFromServer;
//    }


}
