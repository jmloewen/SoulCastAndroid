package com.camvy.kevinzhang.soulcast;

import com.google.gson.annotations.SerializedName;

/**
 * Created by kevinzhang on 2016-12-14.
 */

public class Soul {
    @SerializedName("soulType") private String soulType;
    @SerializedName("s3Key") private String s3Key;
    @SerializedName("epoch") private long epoch;
    @SerializedName("latitude") private float latitude;
    @SerializedName("longitude") private float longitude;
    @SerializedName("radius") private float radius;
    @SerializedName("token") private String token;

    public Soul(String soulType, String s3Key, long epoch, Device userDevice) {
        this.soulType = soulType;
        this.s3Key = s3Key;
        this.epoch = epoch;
        this.latitude = userDevice.getLatitude();
        this.longitude = userDevice.getLongitude();
        this.radius = userDevice.getRadius();
        this.token = userDevice.getToken();
    }

    public void setSoulType(String soulType){this.soulType = soulType;}
    public void sets3Key(String s3Key){this.s3Key = s3Key;}
    public void setEpoch(long epoch){this.epoch = epoch;}
    public String getSoulType(){return soulType;}
    public String gets3Key(){return s3Key;}
    public long getEpoch(){return epoch;}
    public void setRadius(float radius){this.radius = radius;}
    public float getRadius(){return radius;}
}
