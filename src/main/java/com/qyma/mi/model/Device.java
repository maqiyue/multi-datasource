package com.qyma.mi.model;

import lombok.Data;

@Data
public class Device {
    public String bssid;
    public int shareFlag;
    public String latitude;
    public int permitLevel;
    public String pid;
    public boolean isOnline;
    public String ssid;
    public String mac;
    public long uid;
    public int resetFlag;
    public String password;
    public Extra extra;
    public String model;
    public int adminFlag;
    public String longitude;
    public int pdId;
    public String p2pId;
    public int rssi;
    public int familyId;
    public String parentModel;
    public String token;
    public int showMode;
    public String parentId;
    public String name;
    public String localip;
    public String did;
    public String desc;

    @Data
    public static class Extra {
        public int pincodeType;
        public int isSetPincode;
        public int isPasswordEncrypt;
        public String fwVersion;
        public int needVerifyCode;
    }
}
