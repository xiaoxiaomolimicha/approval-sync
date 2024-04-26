package com.erplus.sync.utils;

import com.erplus.sync.entity.DatabaseSingleton;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;

@Slf4j
public class DataSourceSingleton {

    private static volatile HikariDataSource dataSource;

    public static DataSource getDataSource() {
        if (dataSource == null) {
            synchronized (DatabaseSingleton.class) {
                if (dataSource == null) {
                    dataSource = new HikariDataSource();
                    DatabaseSingleton databaseSingleton = DatabaseSingleton.getDatabaseSingleton();
                    dataSource.setJdbcUrl(databaseSingleton.getUrl());
                    dataSource.setDriverClassName(databaseSingleton.getDriverClassName());
                    dataSource.setUsername(databaseSingleton.getUsername());
                    dataSource.setPassword(databaseSingleton.getPassword());
//                    dataSource.setIdleTimeout(60000);
                    dataSource.setAutoCommit(true);
                    dataSource.setMaximumPoolSize(1);
                    dataSource.setMinimumIdle(1);
                    dataSource.setMaxLifetime(60000 * 10);
                    dataSource.setConnectionTestQuery("SELECT 1");
                }
            }
        }
        return dataSource;
    }

    public static void closeDataSource() {
        if (dataSource != null) {
            dataSource.close();
            log.info("我关闭了:数据库连接池");
        }
    }

}
