package com.erobbing.voice.utils;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


/**
 * Gson工具类
 * @author 
 *
 */
public class GsonUtil {
    private static Gson sGson = new GsonBuilder().create();
    
    public static <T> T fromJson(String json, Class<T> classOfT) {
        return sGson.fromJson(json, classOfT);
    }
    
    public static <T> T fromJson(String json, Type classOfT) {
        return sGson.fromJson(json, classOfT);
    }
    
    public static String toJson(Object src) {
        return sGson.toJson(src);
    }
}