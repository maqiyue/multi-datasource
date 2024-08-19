package com.qyma.mi.model;

import lombok.Data;

@Data
public class Audio {
    private String serialNumber;
    private String address;
    private Capabilities capabilities;  // 嵌套类
    private String deviceID;
    private String deviceSNProfile;
    private String ssid;
    private String mac;
    private String deviceProfile;
    private boolean current;
    private String name;
    private String miotDID;
    private String alias;
    private int brokerIndex;
    private String presence;
    private String brokerEndpoint;
    private String hardware;

    public static class Capabilities {
        private String romVersion;
        private String remoteCtrlType;
        // 其他字段省略，根据实际需要添加
    }
}
