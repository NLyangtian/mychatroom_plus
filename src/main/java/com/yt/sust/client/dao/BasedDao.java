package com.yt.sust.client.dao;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.yt.sust.utils.CommUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * 封装基础dao操作---->获取数据源，获取连接，关闭连接，关闭资源等
 */
public class BasedDao {
    private static DruidDataSource dataSource;
    //使用静态代码块，保证数据源在类加载的时候就只加载一次
    static {
        Properties properties=CommUtils.loadProperties("datasource.properties");
        try {
            dataSource=(DruidDataSource) DruidDataSourceFactory.createDataSource(properties);
        } catch (Exception e) {
            System.err.println("加载数据源失败");
            e.printStackTrace();
        }
    }

    //获取连接
    protected DruidPooledConnection getConnection(){
        try {
            return (DruidPooledConnection) dataSource.getPooledConnection();
        } catch (SQLException e) {
            System.err.println("获取连接失败");
            e.printStackTrace();
        }
        return null;
    }

    //关闭资源，关闭连接
    protected  void closeResources(Connection connection, Statement statement){
        if(connection!=null){
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(statement!=null){
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    protected  void closeResources(Connection connection,
                                   Statement statement,
                                   ResultSet resultSet){
        closeResources(connection,statement);
        if(resultSet != null){
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
