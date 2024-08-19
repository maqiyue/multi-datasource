package com.qyma.mi.utils;

public class RandomUtil {

    public static String random(int count) {
        String str = "1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String rnd = "";
        for (int i=0; i<count; i++) {
            rnd += str.charAt((int) Math.floor(Math.random() * str.length()));
        }
        return rnd;
    }

}
