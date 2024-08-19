package com.qyma.mi.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class JsonFileUtil {
    public static  <T> T readJsonFile(File file,String key, Class<T> clazz)  {
        if (!file.exists()) {
            createEmptyJsonFile(file);
        }

        try (FileReader reader = new FileReader(file)) {
            StringBuilder stringBuilder = new StringBuilder();
            int character;
            while ((character = reader.read()) != -1) {
                stringBuilder.append((char) character);
            }
            JSONObject json = JSON.parseObject(stringBuilder.toString());
            if (json != null){
                return JSON.toJavaObject(json.getJSONObject(key), clazz);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static void writeJsonToFile(File file, Object object,String key){
        JSONObject jsonObject = readJsonFile(file, key, JSONObject.class);
        if (jsonObject == null) jsonObject = new JSONObject();
        try (FileWriter fileWriter = new FileWriter(file)) {
            jsonObject.put(key, JSONObject.toJSON(object));
            fileWriter.write(jsonObject.toJSONString());
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private static void createEmptyJsonFile(File file) {
        JSONObject jsonObject = new JSONObject();
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(jsonObject.toJSONString());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception{
        ClassLoader classLoader = new JsonFileUtil().getClass().getClassLoader();
        File file = new File(classLoader.getResource("data.json").getFile());
        Map<String,String> map = new HashMap<>();
        map.put("!","2");
        map.put("3","2");
        writeJsonToFile(file,map,"token");

    }
}
