package com.example.kevinzhang.soulpost;

/**
 * Created by kevinzhang on 2016-12-14.
 */

public class Soul {
    private String soulType;
    private String s3Key;
    private long epoch;
    private double longitude;
    private double latitude;
    private double radius;
    private String token;
    private Device userDevice;

    public Soul(String soulType, String s3Key, long epoch, Device userDevice) {
        this.userDevice = userDevice;
        this.soulType = soulType;
        this.s3Key = s3Key;
        this.epoch = epoch;
        this.latitude = userDevice.getLatitude();
        this.longitude = userDevice.getLongitude();
        this.radius = userDevice.getRadius();
        this.token = userDevice.getToken();
    }

    public void setSoulType(String soulType){this.soulType = soulType;}
    public void setS3Key(String s3Key){this.s3Key = s3Key;}
    public void setEpoch(long epoch){this.epoch = epoch;}
    public void setUserDevice(Device userDevice){this.userDevice = userDevice;}
    public String getSoulType(){return soulType;}
    public String getS3Key(){return s3Key;}
    public long getEpoch(){return epoch;}
    public Device getUserDevice(){return userDevice;}
}
