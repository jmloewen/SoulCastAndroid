package com.example.kevinzhang.soulpost;

/**
 * Created by kevinzhang on 2016-12-10.
 */

public class Device {
    int longitude;
    int latitude;
    int radius;
    String token;

    public Device(int longitude, int latitude, int radius, String token) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.radius = radius;
        this.token = token;
    }
}
