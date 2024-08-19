package com.qyma.mi.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.qyma.mi.model.Device;
import com.qyma.mi.model.Token;
import com.qyma.mi.utils.RandomUtil;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;


import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Thread.sleep;

@Slf4j
@Service
public class MiIotService {

    @Autowired
    Token token;



    /**
     * 设备执行操作
     * https://home.miot-spec.com/
     * @param did name设备ID
     * @param sid 功能分类ID
     * @param pid 设备属性ID
     * @param aid 设备方法ID
     * @param action
     * @param silent
     */
    public void doAction(String did, String sid,String aid, String action,boolean silent){
        JSONObject params = new JSONObject();
        params.put("did",did);
        params.put("siid",sid);
        params.put("aiid",aid);
        JSONObject in = new JSONObject();
        in.put("action",action);
        in.put("silent",silent);
        params.put("in",in);
        JSONObject data = new JSONObject();
        data.put("params",params);
        post("/miotspec/action", data.toJSONString());
    }

    /**
     * 获取设备列表
     */
    @PostConstruct
    @Bean(name = "devicesMap")
    public Map<String, Device> getDeviceList(){
        JSONObject params = new JSONObject();
        params.put("getVirtualModel",false);
        params.put("getHuamiDevices",0);
        JSONObject post = null;
        int maxAttempts = 3;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                post = post("/home/device_list", params.toJSONString());
                if (post != null && post.getInteger("code") != null && post.getInteger("code") == 0){
                    JSONObject result = post.getJSONObject("result");
                    JSONArray array = result.getJSONArray("list");
                    Map<String, Device> devicesMap = new HashMap<>();
                    array.forEach(item -> {
                        Device device = ((JSONObject) item).toJavaObject(Device.class);
                        devicesMap.put(device.getName(),device);
                    });
                    return devicesMap;
                }

            } catch (Exception e) {
                log.info("获取设备列表失败，10秒后重新尝试");
                if (attempt == maxAttempts) {
                    throw new RuntimeException("无法获取设备列表,请稍后重启");
                }
                try {
                    sleep(10*000);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
        return new HashMap<>();
    }

    /**
     * 获取设备属性
     */
    public void getDeviceAttributes(){
        post("/miotspec/prop/get", "{\"params\":[{\"did\":\"545290485\",\"siid\":2,\"piid\":0},{\"did\":\"545290485\",\"siid\":2,\"piid\":1}]}");
    }

    /**
     * 设置设备属性
     */
    public void setDeviceAttributes(){
        //post("/miotspec/prop/set", "{\"params\":[{\"did\":\"111111111\",\"siid\":2,\"piid\":1,\"value\":true},{\"did\":\"111111111\",\"siid\":2,\"piid\":6,\"value\":70}]}");
    }

    /**
     * 获取房间列表
     */
    public void getRoomList(){
        //post("/v2/homeroom/gethome", "{\"fg\":false,\"fetch_share\":true,\"fetch_share_dev\":true,\"limit\":300,\"app_ver\":7}");
    }

    /**
     * 获取设备耗材  owner_id即userId
     */
    public void getDeviceConsumables(){
        //post("/v2/home/standard_consumable_items", "{\"home_id\":111111111,\"owner_id\":111111111}");
    }

    /**
     * 获取红外遥控器的按键列表
     */
    public void getRemoteControlButtons(){
        //post("/v2/irdevice/controller/keys", "{\"did\":\"ir.111111111\"}");
    }

    /**
     * 触发红外遥控器按键
     */
    public void executeRedRemoteControl(){
        //post("/v2/irdevice/controller/key/click", "{\"did\": \"ir.111111111\", \"key_id\": 100000001}");
    }

    /**
     * 获取场景列表(包含手动场景和自动化)
     */
    public void getSceneList(){
        //post("/appgateway/miot/appsceneservice/AppSceneService/GetSceneList", "{\"home_id\":\"111111111\"}");
    }

    /**
     * 执行手动场景
     */
    public void executeManualScene(){
        //post("/appgateway/miot/appsceneservice/AppSceneService/RunScene", "{\"scene_id\":\"111111111\",\"trigger_key\":\"user.click\"}");
    }

    public static String generateNonce() {
        return RandomUtil.random(16);
    }

    public static String generateSignedNonce(String secret, String nonce) throws Exception {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        messageDigest.update(Base64.getDecoder().decode(secret));
        messageDigest.update(Base64.getDecoder().decode(nonce));
        return Base64.getEncoder().encodeToString(messageDigest.digest());
    }

    public static String generateSignature(String url, String signedNonce, String nonce, String data) throws Exception {
        Mac hmac = Mac.getInstance("HmacSHA256");
        String sign = url+"&"+signedNonce+"&"+nonce+"&data="+data;
        hmac.init(new SecretKeySpec(Base64.getDecoder().decode(signedNonce), "HmacSHA256"));
        return Base64.getEncoder().encodeToString(hmac.doFinal(sign.getBytes("utf-8")));
    }

    public JSONObject post(String uri, String data) {
        try {
            OkHttpClient client = new OkHttpClient();
            String nonce = generateNonce();
            String signedNonce = generateSignedNonce(token.getSecurityToken(), nonce);
            String signature = generateSignature(uri, signedNonce, nonce, data);

            FormBody body = new FormBody.Builder()
                    .add("_nonce", nonce)
                    .add("data", data)
                    .add("signature", signature)
                    .build();

            Request request = new Request.Builder()
                    .url("https://api.io.mi.com/app" + uri)
                    .post(body)
                    .header("User-Agent", "APP/com.xiaomi.mihome APPV/6.0.103 iosPassportSDK/3.9.0 iOS/14.4 miHSTS")
                    .header("x-xiaomi-protocal-flag-cli", "PROTOCAL-HTTP2")
                    .header("Cookie", "PassportDeviceId="+token.getDeviceId()+";userId="+token.getUserId()+";serviceToken="+token.getServiceToken()+";")
                    .build();

            Call call = client.newCall(request);
            Response response = call.execute(); // 同步执行请求
            if (response.isSuccessful()) {
                JSONObject result = JSONObject.parseObject(response.body().string());
                return result;
            }
                //异步调用 暂时不用
//            call.enqueue(new Callback() {
//                @Override
//                public void onFailure(Call call, IOException e) {
//                    e.printStackTrace();
//                }
//
//                @Override
//                public void onResponse(Call call, Response response) throws IOException {
//                    JSONObject result = JSONObject.parseObject(response.body().string());
//                    return;result r
//                }
//            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
