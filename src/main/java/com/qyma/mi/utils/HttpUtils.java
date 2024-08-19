package com.qyma.mi.utils;

import com.alibaba.fastjson.JSONObject;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import com.alibaba.fastjson.JSONObject;
import okhttp3.*;

import java.io.IOException;

public class HttpUtils {

    private static final OkHttpClient client = new OkHttpClient();

    public static JSONObject sendGetRequest(String url, Headers headers) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .headers(headers)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            String responseBody = response.body().string();
            return JSONObject.parseObject(responseBody);
        }
    }

    public static JSONObject sendPostRequest(String url, JSONObject data, Headers headers) throws IOException {
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), data.toJSONString());

        Request request = new Request.Builder()
                .url(url)
                .headers(headers)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            String responseBody = response.body().string();
            return JSONObject.parseObject(responseBody);
        }
    }

    public static void main(String[] args) {
        try {
            // 示例 GET 请求
            String getUrl = "https://jsonplaceholder.typicode.com/posts/1";
            Headers getHeaders = new Headers.Builder()
                    .add("Authorization", "Bearer your_token_here")
                    .add("Content-Type", "application/json")
                    .build();
            JSONObject getResponse = sendGetRequest(getUrl, getHeaders);
            System.out.println("GET 请求返回结果：" + getResponse.toJSONString());

            // 示例 POST 请求
            String postUrl = "https://jsonplaceholder.typicode.com/posts";
            JSONObject postData = new JSONObject();
            postData.put("title", "foo");
            postData.put("body", "bar");
            postData.put("userId", 1);

            Headers postHeaders = new Headers.Builder()
                    .add("Authorization", "Bearer your_token_here")
                    .add("Content-Type", "application/json")
                    .build();

            JSONObject postResponse = sendPostRequest(postUrl, postData, postHeaders);
            System.out.println("POST 请求返回结果：" + postResponse.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
