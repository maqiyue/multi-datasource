//package com.qyma.mi.config;
//
//import com.alibaba.fastjson.JSONObject;
//import com.qyma.mi.model.Device;
//import com.qyma.mi.model.Token;
//import com.qyma.mi.model.User;
//import com.qyma.mi.service.MiIOTService;
//import com.qyma.mi.service.TokenManager;
//import com.qyma.mi.utils.JsonFileUtil;
//import com.qyma.mi.utils.RandomUtil;
//import lombok.extern.slf4j.Slf4j;
//import okhttp3.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.io.File;
//import java.net.URLEncoder;
//import java.security.MessageDigest;
//import java.util.Base64;
//import java.util.List;
//import java.util.Map;
//
//import static java.lang.Thread.sleep;
//
//@Slf4j
//@Configuration
//public class AppConfig {
//
//
//
//
//    @Bean
//    public Token token(TokenManager tokenManager) {
//        return tokenManager.initToken();
//    }
//
//    @Bean
//    public Map<String, Device> devicesMap(MiIOTService miIOTService) {
//        return miIOTService.getDeviceList();
//    }
//
//
//
//
//
//
//
//
//
//
//}
