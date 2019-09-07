package com.yt.sust.utils;

import com.yt.sust.entity.User;
import org.junit.Assert;
import org.junit.Test;

import java.util.Properties;

import static org.junit.Assert.*;

public class CommUtilsTest {

    @Test
    public void loadProperties() {
        String fileName="datasource.properties";
        Properties properties=CommUtils.loadProperties(fileName);
        Assert.assertNotNull(properties);
    }

    @Test
    public void object2Json() {
        User user=new User();
        user.setId(1);
        user.setUserName("tom");
        user.setPassWord("123456");
        user.setBrief("hehe");
        String str=CommUtils.object2Json(user);
        System.out.println(str);
    }

    @Test
    public void json2Object() {
        String str="{\"id\":1,\"userName\":\"tom\",\"passWord\":\"123456\",\"brief\":\"hehe\"}";
        User user=(User)CommUtils.json2Object(str,User.class);
        System.out.println(user.getUserName());
    }
}