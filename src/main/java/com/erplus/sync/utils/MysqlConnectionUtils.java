package com.erplus.sync.utils;

import com.jcraft.jsch.Session;
import com.erplus.sync.entity.DatabaseEntity;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.*;

@Slf4j
public class MysqlConnectionUtils {
    public static final int MYSQL_LOCAL_PORT = 3307; // 本地监听的端口
    private static Connection connection;


    public static Connection getMysqlConnection() throws Throwable{
        if (connection != null) {
            return connection;
        }
        Properties properties = JschSessionUtils.getProperties();
        DatabaseEntity databaseConfig = JschSessionUtils.buildDatabaseConfig(properties);

        // 创建端口转发通道
        Session session = JschSessionUtils.getJschSession(properties);
        session.setPortForwardingL(MYSQL_LOCAL_PORT, databaseConfig.getHost(), databaseConfig.getPort());

        // 使用JDBC连接到数据库
        String url = "jdbc:" + databaseConfig.getProtocol() + "://localhost:" + MYSQL_LOCAL_PORT + "/" + databaseConfig.getDatabase();
        connection = DriverManager.getConnection(url, databaseConfig.getUsername(), databaseConfig.getPassword());
        return connection;
    }

    public static void closeConnection(){
      try {
          if (connection != null) {
              connection.close();
          }
      } catch (Throwable e) {
          log.error(e.getMessage(), e);
      }
    }
}
