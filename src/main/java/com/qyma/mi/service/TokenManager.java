package com.qyma.mi.service;

import com.qyma.mi.model.Token;
import com.qyma.mi.model.MiConfig;
import com.qyma.mi.utils.JsonFileUtil;
import com.qyma.mi.utils.RandomUtil;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;


import java.io.File;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.List;

import static java.lang.Thread.sleep;

@Slf4j
@Service
public class TokenManager {

    private static final String JSON_FILE_PATH = "data.json";
    private static final String TOKEN_KEY = "token";
    private static final String DEVICES_KEY = "devices";

    @Autowired
    private MiConfig miConfig;

    @Autowired
    ResourceLoader resourceLoader;
    @PostConstruct
    @Bean
    public Token initToken() throws Exception{
        Resource resource = resourceLoader.getResource("classpath:"+JSON_FILE_PATH);
        File file = resource.getFile();
        Token token = JsonFileUtil.readJsonFile(file,TOKEN_KEY, Token.class);
        if (token == null || token.getSecurityToken() == null || token.getServiceToken() == null){
            int maxAttempts = 3;
            for (int attempt = 1; attempt <= maxAttempts; attempt++) {
                try {
                    token = this.login();
                    JsonFileUtil.writeJsonToFile(file,token,TOKEN_KEY);
                    break;
                } catch (Exception e) {
                    log.info("获取token失败，10秒后重新尝试");
                    if (attempt == maxAttempts) {
                        throw new RuntimeException("无法获取token,请稍后重启");
                    }
                    try {
                        sleep(10*000);
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        }
        return token;
    }

    public Token login() throws Exception {
        OkHttpClient client = new OkHttpClient();
        Request request = null;
        Response response = null;
        JSONObject result = null;
        //xiaomiio
        request = new Request.Builder()
                .url("https://account.xiaomi.com/pass/serviceLogin?sid=" + "micoapi" + "&_json=true")
                .header("User-Agent", "APP/com.xiaomi.mihome APPV/6.0.103 iosPassportSDK/3.9.0 iOS/14.4 miHSTS")
                .get()
                .build();
        response = client.newCall(request).execute();
        result = JSONObject.parseObject(response.body().string().substring(11));


        FormBody body = new FormBody.Builder()
                .add("qs", result.getString("qs"))
                .add("sid", result.getString("sid"))
                .add("_sign", result.getString("_sign"))
                .add("callback", result.getString("callback"))
                .add("user", miConfig.getUserId())
                .add("hash", miConfig.getPasswordMd5().toUpperCase())
                .add("_json", "true")
                .build();
        request = new Request.Builder()
                .url("https://account.xiaomi.com/pass/serviceLoginAuth2")
                .header("User-Agent", "APP/com.xiaomi.mihome APPV/6.0.103 iosPassportSDK/3.9.0 iOS/14.4 miHSTS")
                .post(body)
                .build();
        response = client.newCall(request).execute();
        result = JSONObject.parseObject(response.body().string().substring(11));
        if (result.getIntValue("code") != 0) {
            return new Token();
        }
        String nonce = result.getString("nonce");
        String location = result.getString("location");
        String userId = result.getString("userId");
        String securityToken = result.getString("ssecurity");

        String n = "nonce=" + nonce + "&" + securityToken;
        MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
        messageDigest.update(n.getBytes("utf-8"));
        request = new Request.Builder()
                .url(location + "&clientSign=" + URLEncoder.encode(Base64.getEncoder().encodeToString(messageDigest.digest()), "utf-8"))
                .header("User-Agent", "APP/com.xiaomi.mihome APPV/6.0.103 iosPassportSDK/3.9.0 iOS/14.4 miHSTS")
                .get()
                .build();
        response = client.newCall(request).execute();
        List<Cookie> cookies = Cookie.parseAll(request.url(), response.headers());
        Token tokenProperties = new Token();
        for (Cookie cookie : cookies) {
            if ("serviceToken".equals(cookie.name())) tokenProperties.setServiceToken(cookie.value());
        }
        tokenProperties.setSecurityToken(securityToken);
        tokenProperties.setDeviceId( RandomUtil.random(16));
        tokenProperties.setUserId(userId);
        return tokenProperties;
    }
}
