//package com.qyma.mi.service;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import okhttp3.*;
//
//import java.io.IOException;
//import java.util.UUID;
//
//public class MiNA {
//
//    private Account account;
//
//    public MiNA(Account account) {
//        this.account = account;
//    }
//
//    public static Account getDevice(Account account) throws IOException {
//        if (!account.getSid().equals("micoapi")) {
//            return account;
//        }
//
//        JSONObject data = new JSONObject();
//        data.put("requestId", UUID.randomUUID().toString());
//        data.put("timestamp", System.currentTimeMillis() / 1000);
//
//        String url = "https://api2.mina.mi.com/admin/v2/device_list";
//        OkHttpClient client = new OkHttpClient();
//        RequestBody body = RequestBody.create(MediaType.parse("application/json"), data.toJSONString());
//
//        Request request = new Request.Builder()
//                .url(url)
//                .header("User-Agent", "MICO/AndroidApp/@SHIP.TO.2A2FE0D7@/2.4.40")
//                .header("Content-Type", "application/json")
//                .post(body)
//                .build();
//
//        Response response = client.newCall(request).execute();
//        if (!response.isSuccessful()) {
//            throw new IOException("Unexpected code " + response);
//        }
//
//        String responseBody = response.body().string();
//        JSONObject devices = JSON.parseObject(responseBody);
//
//        if (Debugger.enableTrace) {
//            System.out.println("\uD83D\uDC1B MiNA 设备列表: " + devices.toJSONString());
//        }
//
//        JSONObject device = devices.getJSONArray("data").stream()
//                .map(obj -> (JSONObject) obj)
//                .filter(e -> e.getString("deviceID").equals(account.getDid()) ||
//                        e.getString("miotDID").equals(account.getDid()) ||
//                        e.getString("name").equals(account.getDid()) ||
//                        e.getString("alias").equals(account.getDid()))
//                .findFirst()
//                .orElse(null);
//
//        if (device != null) {
//            account.setDevice(device.getJSONObject("deviceID"));
//        }
//
//        return account;
//    }
//
//    public JSONObject ubus(String scope, String command, JSONObject message) throws IOException {
//        JSONObject requestData = new JSONObject();
//        requestData.put("deviceId", account.getDevice().getString("deviceId"));
//        requestData.put("path", scope);
//        requestData.put("method", command);
//        requestData.put("message", message);
//
//        return _callMina("POST", "/remote/ubus", requestData);
//    }
//
//    public JSONObject getDevices() throws IOException {
//        return _callMina("GET", "/admin/v2/device_list", null);
//    }
//
//    public JSONObject getStatus() throws IOException {
//        JSONObject data = ubus("mediaplayer", "player_get_play_status", null);
//        JSONObject res = JSON.parseObject(data.getString("info"));
//
//        if (data == null || data.getInteger("code") != 0 || res == null) {
//            return null;
//        }
//
//        JSONObject map = new JSONObject();
//        map.put("0", "idle");
//        map.put("1", "playing");
//        map.put("2", "paused");
//        map.put("3", "stopped");
//
//        res.put("status", map.getString(res.getString("status")));
//        res.put("volume", res.getInteger("volume"));
//
//        return res;
//    }
//
//    public Integer getVolume() throws IOException {
//        JSONObject data = getStatus();
//        return data != null ? data.getInteger("volume") : null;
//    }
//
//    public Boolean setVolume(Integer volume) throws IOException {
//        volume = Math.round(Math.min(Math.max(volume, 6), 100));
//        JSONObject res = ubus("mediaplayer", "player_set_volume", new JSONObject().fluentPut("volume", volume));
//        return res != null && res.getInteger("code") == 0;
//    }
//
//    public Boolean play(String tts, String url) throws IOException {
//        JSONObject res;
//        if (tts != null) {
//            res = ubus("mibrain", "text_to_speech", new JSONObject().fluentPut("text", tts).fluentPut("save", 0));
//        } else if (url != null) {
//            res = ubus("mediaplayer", "player_play_url", new JSONObject().fluentPut("url", url).fluentPut("type", 1));
//        } else {
//            res = ubus("mediaplayer", "player_play_operation", new JSONObject().fluentPut("action", "play"));
//        }
//        return res != null && res.getInteger("code") == 0;
//    }
//
//    public Boolean pause() throws IOException {
//        JSONObject res = ubus("mediaplayer", "player_play_operation", new JSONObject().fluentPut("action", "pause"));
//        return res != null && res.getInteger("code") == 0;
//    }
//
//    public Boolean playOrPause() throws IOException {
//        JSONObject res = ubus("mediaplayer", "player_play_operation", new JSONObject().fluentPut("action", "toggle"));
//        return res != null && res.getInteger("code") == 0;
//    }
//
//    public Boolean stop() throws IOException {
//        JSONObject res = ubus("mediaplayer", "player_play_operation", new JSONObject().fluentPut("action", "stop"));
//        return res != null && res.getInteger("code") == 0;
//    }
//
//    /**
//     * 注意：
//     * 只拉取用户主动请求，设备被动响应的消息，
//     * 不包含设备主动回应用户的消息。
//     *
//     * - 从游标处由新到旧拉取
//     * - 结果包含游标消息本身
//     * - 消息列表从新到旧排序
//     */
//    public JSONObject getConversations(int limit, Long timestamp) throws IOException {
//        JSONObject requestData = new JSONObject();
//        requestData.put("limit", limit);
//        requestData.put("timestamp", timestamp);
//        requestData.put("requestId", UUID.randomUUID().toString());
//        requestData.put("source", "dialogu");
//        requestData.put("hardware", account.getDevice().getString("hardware"));
//
//        String url = "https://userprofile.mina.mi.com/device_profile/v2/conversation";
//        OkHttpClient client = new OkHttpClient();
//        RequestBody body = RequestBody.create(MediaType.parse("application/json"), requestData.toJSONString());
//
//        Request request = new Request.Builder()
//                .url(url)
//                .header("User-Agent", "Mozilla/5.0 (Linux; Android 10; 000; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/119.0.6045.193 Mobile Safari/537.36 /XiaoMi/HybridView/ micoSoundboxApp/i appVersion/A_2.4.40")
//                .header("Referer", "https://userprofile.mina.mi.com/dialogue-note/index.html")
//                .header("Content-Type", "application/json")
//                .post(body)
//                .build();
//
//        Response response = client.newCall(request).execute();
//        if (!response.isSuccessful()) {
//            throw new IOException("Unexpected code " + response);
//        }
//
//        String responseBody = response.body().string();
//        JSONObject res = JSON.parseObject(responseBody);
//
//        if (res.getInteger("code") != 0 && Debugger.enableTrace) {
//            System.out.println("\u274C getConversations failed: " + res.toJSONString());
//        }
//
//        return res;
//    }
//
//    // 辅助方法
//    private JSONObject _callMina(String method, String path2, JSONObject data) throws IOException {
//        JSONObject requestData = new JSONObject();
//        requestData.put("requestId", UUID.randomUUID().toString());
//        requestData.put("timestamp", System.currentTimeMillis() / 1000);
//
//        String url = "https://api2.mina.mi.com" + path2;
//        OkHttpClient client = new OkHttpClient();
//        RequestBody body = RequestBody.create(MediaType.parse("application/json"), requestData.toJSONString());
//
//        Request.Builder requestBuilder = new Request.Builder()
//                .url(url)
//                .header("User-Agent", "MICO/AndroidApp/@SHIP.TO.2A2FE0D7@/2.4.40")
//                .header("Content-Type", "application/json");
//
//        if (method.equals("GET")) {
//            requestBuilder.get();
//        } else if (method.equals("POST")) {
//            requestBuilder.post(body);
//        }
//
//        Request request = requestBuilder.build();
//        Response response = client.newCall(request).execute();
//        if (!response.isSuccessful()) {
//            throw new IOException("Unexpected code " + response);
//        }
//
//        String responseBody = response.body().string();
//        JSONObject res = JSON.parseObject(responseBody);
//
//        if (res.getInteger("code") != 0 && Debugger.enableTrace) {
//            System.out.println("\u274C _callMina failed: " + res.toJSONString());
//        }
//
//        return res;
//    }
//
//    // 示例用法
//    public static void main(String[] args) {
//        Account account = new Account();  // 假设有一个合适的 Account 类
//
//        try {
//            MiNA mina = new MiNA(account);
//            account = MiNA.getDevice(account);
//            System.out.println("设备信息：" + JSON.toJSONString(account.getDevice()));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    // Account 类的定义，仅作为示例，具体字段和方法需要根据实际情况定义
//    static class Account {
//        private String sid;
//        private String did;
//        private JSONObject device;
//
//        public String getSid() {
//            return sid;
//        }
//
//        public void setSid(String sid) {
//            this.sid = sid;
//        }
//
//        public String getDid() {
//            return did;
//        }
//
//        public void setDid(String did) {
//            this.did = did;
//        }
//
//        public JSONObject getDevice() {
//            return device;
//        }
//
//        public void setDevice(JSONObject device) {
//            this.device = device;
//        }
//    }
//
//    // Debugger 类的定义，仅作为示例
//    static class Debugger {
//        public static boolean enableTrace = true;  // 是否启用调试输出
//    }
//}
