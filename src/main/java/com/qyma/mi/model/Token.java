package com.qyma.mi.model;

import lombok.Data;

@Data
public class Token {
    private String securityToken;
    private String serviceToken;
    private String deviceId;
    private String userId;
}
