package com.yt.sust.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/*
封装公共工具方法，如加载配置文件、json序列化等
 */


public class CommUtils {

    //利用第三方库（GSON库，实现序列化和反序列化）
    private  static  final Gson GSON = new GsonBuilder().create();
    /**
     * 加载配置文件
     * @param fileName 要加载的配置文件的名称
     * @return
     */
    public  static Properties loadProperties(String fileName){
        Properties properties=new Properties();
        //加载指定的文件-->输入流
        InputStream in=CommUtils.class.getClassLoader().getResourceAsStream(fileName);
        try {
            properties.load(in);
        } catch (IOException e) {
            return null;
        }
        return properties;
    }

    /**
     * 将任意对象序列化为字符串
     * @param obj
     * @return
     */

    public static  String object2Json(Object obj){
        return GSON.toJson(obj);
    }


    /**
     * 将json字符串反序列化为对象
     * @param jsonStr--->传入的json字符串
     * @param objClass
     * @return
     */
    public  static  Object json2Object(String  jsonStr,Class objClass){
        return  GSON.fromJson(jsonStr,objClass);
    }
}
