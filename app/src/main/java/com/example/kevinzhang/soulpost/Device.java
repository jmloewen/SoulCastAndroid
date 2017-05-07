package com.example.kevinzhang.soulpost;

import com.google.gson.annotations.SerializedName;

/**
 * Created by kevinzhang on 2016-12-10.
 */

public class Device {
    @SerializedName("os") private String os;
    @SerializedName("longitude") private float longitude;
    @SerializedName("latitude") private float latitude;
    @SerializedName("radius") private float radius;
    @SerializedName("token") private String token;
    private static int id;

    public Device(String os, float latitude, float longitude, float radius, String token) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.radius = radius;
        this.token = token;
        this.os = os;
    }

    public void setLongitude(float longitude){this.longitude = longitude;}
    public void setLatitude(float latitude){this.latitude = latitude;}
    public void setRadius(float radius){this.radius = radius;}
    public void setToken(String token){this.token = token;}
    public void setId(int id){this.id = id;}
    public float getLongitude(){return longitude;}
    public float getLatitude(){return latitude;}
    public float getRadius(){return radius;}
    public String getToken(){return token;}
    public int getId(){return id;}



//    public void setID(int idFromServer){
//        id = idFromServer;
//    }


}
