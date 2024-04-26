package com.erplus.sync.entity;

import com.erplus.sync.utils.JschSessionUtils;
import com.erplus.sync.utils.MysqlConnectionUtils;
import com.erplus.sync.utils.PropertiesSingleton;
import lombok.extern.slf4j.Slf4j;

import java.util.Properties;

@Slf4j
public class DatabaseSingleton {
    private final String host;
    private final int port;
    private final String protocol;
    private final String database;
    private final String username;
    private final String password;

    private volatile static DatabaseSingleton databaseSingleton;

    private DatabaseSingleton(String host, int port, String protocol, String database, String username, String password) {
        this.host = host;
        this.port = port;
        this.protocol = protocol;
        this.database = database;
        this.username = username;
        this.password = password;
    }

    public static DatabaseSingleton getDatabaseSingleton(){
        if (databaseSingleton == null) {
            synchronized (DatabaseSingleton.class) {
                if (databaseSingleton == null) {
                    Properties properties = PropertiesSingleton.getProperties();
                    String mysqlHost = properties.getProperty("mysqlHost");
                    String mysqlUsername = properties.getProperty("mysqlUsername");
                    String mysqlPassword = properties.getProperty("mysqlPassword");
                    int mysqlPort = Integer.parseInt(properties.getProperty("mysqlPort"));
                    String database = properties.getProperty("database");
                    databaseSingleton = new DatabaseSingleton(mysqlHost, mysqlPort, "mysql", database, mysqlUsername, mysqlPassword);
                    log.info("我初始化了databaseSingleton");
                    return databaseSingleton;
                }
            }
        }
        return databaseSingleton;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getDatabase() {
        return database;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getUrl() {
        return "jdbc:" + this.protocol + "://localhost:" + MysqlConnectionUtils.MYSQL_LOCAL_PORT + "/" + database + "?serverTimezone=Asia/Shanghai";
    }

    public String getDriverClassName() {
        return "com.mysql.cj.jdbc.Driver";
    }
}
