package com.yt.sust.client.dao;

import com.yt.sust.entity.User;
import org.apache.commons.codec.digest.DigestUtils;

import java.sql.*;

public class AccountDao extends BasedDao {

    /**
     * 用户注册
     * @param user 注册的对象
     * @return
     */
    public boolean userReg(User user){
        Connection connection=null;
        PreparedStatement statement=null;

        try{
            connection=getConnection();
            String sql="insert into user (username,password,brief)"+
                    "values (?,?,?)";
            statement=connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
            statement.setString(1,user.getUserName());
            statement.setString(2,DigestUtils.md5Hex(user.getPassWord()));
            statement.setString(3,user.getBrief());
            int rows=statement.executeUpdate();
            if(rows == 1)
                return true;
        }catch (SQLException e){
            System.err.println("用户注册失败！");
            e.printStackTrace();
        }finally {
            closeResources(connection,statement);
        }
        return false;
    }

    //用户登录
    public User userLogin(String userName,String passWord){
        Connection connection=null;
        PreparedStatement statement=null;
        ResultSet resultSet=null;

        try{
            connection=getConnection();
            String sql="select * from user where username = ? and password = ?";
            statement=connection.prepareStatement(sql);
            statement.setString(1,userName);
            statement.setString(2,DigestUtils.md5Hex(passWord));
            resultSet=statement.executeQuery();
            if(resultSet.next()){
                User user=getUser(resultSet);
                return user;
            }

        }catch (SQLException e){
            System.err.println("用户登录失败！");
            e.printStackTrace();
        }finally {
            closeResources(connection,statement,resultSet);
        }
        return null;
    }

    private User getUser(ResultSet resultSet) throws SQLException {
        User user=new User();
        user.setId(resultSet.getInt("id"));
        user.setUserName(resultSet.getString("username"));
        user.setPassWord(resultSet.getString("password"));
        user.setBrief(resultSet.getString("brief"));
        return user;
    }
}

