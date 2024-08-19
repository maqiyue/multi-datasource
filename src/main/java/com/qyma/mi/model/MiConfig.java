package com.qyma.mi.model;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "mi")
public class MiConfig {
    private String userId;
    private String passwordMd5;
    private String audioName;

}
