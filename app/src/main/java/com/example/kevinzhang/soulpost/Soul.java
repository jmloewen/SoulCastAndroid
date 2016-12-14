package com.example.kevinzhang.soulpost;

/**
 * Created by kevinzhang on 2016-12-14.
 */

public class Soul {
    String soulType;
    String s3Key;
    int epoch;
    double longitude;
    double latitude;
    double radius;
    String token;

    public Soul(String soulType, String s3Key, int epoch, double longitude, double latitude, double radius, String token) {
        this.soulType = soulType;
        this.s3Key = s3Key;
        this.epoch = epoch;
        this.longitude = longitude;
        this.latitude = latitude;
        this.radius = radius;
        this.token = token;
    }
}
