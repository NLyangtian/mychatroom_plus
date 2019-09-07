package com.yt.sust;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.yt.sust.utils.CommUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Assert;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Properties;

public class JDBCTest {
    private static DruidDataSource dataSource;
    static {
        Properties props=CommUtils.loadProperties("datasource.properties");
        try {
            dataSource=(DruidDataSource) DruidDataSourceFactory.createDataSource(props);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testQuery(){
        Connection connection=null;
        PreparedStatement statement=null;
        ResultSet resultSet=null;

        try{
            connection=(Connection) dataSource.getPooledConnection();
            String sql="select  * from user";
            statement=connection.prepareStatement(sql);
            resultSet=statement.executeQuery();
            while(resultSet.next()){
                int id=resultSet.getInt("id");
                String name=resultSet.getString("username");
                String password=resultSet.getString("password");
                String brief=resultSet.getString("brief");
                System.out.println("id为："+id+"\t"
                                    +"用户名为："+"name"+"\t"
                                    +"密码为："+password+"\t"
                                    +"个性签名为："+brief);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            closResources(connection,statement,resultSet);
        }
    }

    @Test
    public  void testInsert(){
        Connection connection=null;
        PreparedStatement statement=null;


        try{
            connection=(Connection) dataSource.getPooledConnection();
            String password=DigestUtils.md5Hex("123456");
            String sql="insert into user (username,password,brief)"+
                    "values (?,?,?)";
            statement=connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
            statement.setString(1,"李四");
            statement.setString(2,password);
            statement.setString(3,"call me MrLi");
            int rows=statement.executeUpdate();
            Assert.assertEquals(1,rows);

        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            closeResources(connection,statement);
        }
    }

    @Test

    public void testLogin(){
        String userName="李四' -- ";
        String passWord=DigestUtils.md5Hex("1234565");
        Connection connection=null;
        Statement statement=null;
        ResultSet resultSet=null;

        try{
            String sql="select * from user where username = '"+userName+"'"+
                    "and password = '"+passWord+"'";
            connection=(Connection) dataSource.getPooledConnection();
            statement=connection.createStatement();
            resultSet=statement.executeQuery(sql);
            if(resultSet.next()){
                System.out.println("login successful！");
            }else {
                System.out.println("login failure");
            }
        }catch(SQLException e){
            e.printStackTrace();
        }finally {
            closeResources(connection,statement);
        }

    }
    public void  closeResources(Connection connection,
                                Statement statement){
        if(connection!=null){
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(statement !=null){
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public void closResources(Connection connection,
                              Statement statement,
                              ResultSet resultSet){
        closeResources(connection,statement);
        if(resultSet!=null){
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

