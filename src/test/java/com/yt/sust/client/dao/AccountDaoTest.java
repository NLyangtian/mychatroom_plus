package com.yt.sust.client.dao;

import com.yt.sust.entity.User;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class AccountDaoTest {
    private AccountDao accountDao=new AccountDao();
    @Test
    public void userReg() {
        User user= new User();
        user.setUserName("赵五");
        user.setPassWord("abcde");
        user.setBrief("哈哈");
        boolean flag= accountDao.userReg(user);
        Assert.assertTrue(flag);
    }

    @Test
    public void userLogin() {
        String userName="赵五";
        String passWord="abcde";
        User user=accountDao.userLogin(userName,passWord);
        System.out.println(user);
        Assert.assertNotNull(user);
    }
}