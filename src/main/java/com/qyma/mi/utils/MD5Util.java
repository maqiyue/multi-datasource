package com.qyma.mi.utils;

import java.math.BigInteger;
import java.security.MessageDigest;

public class MD5Util {
    public static String encrypt(String password) {
        byte[] bytes = null;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(password.getBytes());//加密
            bytes = messageDigest.digest();//获得加密结果
        } catch (Exception e) {
            e.printStackTrace();
        }
        String result = new BigInteger(1, bytes).toString(16);
        for (int i = 0; i < 32 - result.length(); i++) {
            result = "0" + result;
        }
        return result;
    }

    public static void main(String[] args) {
        System.out.println(encrypt("Yuege2018...."));
    }
}
