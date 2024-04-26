package com.erplus.sync.utils;

import com.erplus.sync.entity.DatabaseSingleton;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.*;

@Slf4j
public class MysqlConnectionUtils {
    public static final int MYSQL_LOCAL_PORT = 3307; // 本地监听的端口
    private static volatile Connection connection;

    public static Connection getMysqlConnection() throws Throwable{
        if (connection == null) {
            synchronized (MysqlConnectionUtils.class) {
                if (connection == null) {
                    DatabaseSingleton databaseSingleton = DatabaseSingleton.getDatabaseSingleton();
                    // 创建端口转发通道
                    ForwardPortUtils.forwardMysqlPort();
                    // 使用JDBC连接到数据库
                    connection = DriverManager.getConnection(databaseSingleton.getUrl(), databaseSingleton.getUsername(), databaseSingleton.getPassword());
                }
            }
        }
        return connection;
    }

    public static void closeConnection(){
      try {
          if (connection != null) {
              connection.close();
              log.info("我关闭了原生jdbc的连接");
          }
      } catch (Throwable e) {
          log.error(e.getMessage(), e);
      }
    }
}
