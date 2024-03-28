package com.erplus.sync.utils;

import com.erplus.sync.entity.DatabaseEntity;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.erplus.sync.entity.SshTunnelEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

@Slf4j
public class JschSessionUtils {

    private static Session session;
    public static SshTunnelEntity buildSshTunnelConfig(Properties properties) throws Throwable{
        String host = properties.getProperty("jumpHost");
        String username = properties.getProperty("jumpUsername");
        String password = properties.getProperty("jumpPassword");
        return new SshTunnelEntity(username, host, 22, password);
    }

    public static DatabaseEntity buildDatabaseConfig(Properties properties) throws Throwable {
        String mysqlHost = properties.getProperty("mysqlHost");
        String mysqlUsername = properties.getProperty("mysqlUsername");
        String mysqlPassword = properties.getProperty("mysqlPassword");
        String database = properties.getProperty("database");
        return new DatabaseEntity(mysqlHost, 3306, "mysql", database, mysqlUsername, mysqlPassword);
    }

    public static Properties getProperties() throws Throwable {
        URL url = MysqlConnectionUtils.class.getClassLoader().getResource("variable.properties");
        Properties properties = new Properties();
        Validate.notNull(url, "variable.properties not exist");
        properties.load(Files.newInputStream(Paths.get(url.getPath())));
        return properties;
    }

    public static Session getJschSession(Properties properties) throws Throwable{
        if (session != null) {
            return session;
        }
        SshTunnelEntity sshTunnelConfig = JschSessionUtils.buildSshTunnelConfig(properties);
        JSch jSch = new JSch();
        Session jschSession = jSch.getSession(sshTunnelConfig.getUsername(), sshTunnelConfig.getJumpHost(), sshTunnelConfig.getJumpPort());
        jschSession.setPassword(sshTunnelConfig.getPassword());
        jschSession.setConfig("StrictHostKeyChecking", "no");
        jschSession.connect();
        session = jschSession;
        return session;
    }

    public static Session getJschSession() throws Throwable {
        if (session != null) {
            return session;
        }
        return getJschSession(getProperties());
    }

    public static void closeSession() {
        if (session != null) {
            try {
                session.delPortForwardingL(MysqlConnectionUtils.MYSQL_LOCAL_PORT);
                session.delPortForwardingL(EsClientUtils.ES_LOCAL_PORT);
            } catch (Throwable e) {
                log.error(e.getMessage(), e);
            } finally {
                session.disconnect();
            }
        }
    }

    public static void closeSession(int ...localPort) {
        if (session != null) {
            try {
                for (int port : localPort) {
                    session.delPortForwardingL(port);
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            } finally {
                session.disconnect();
            }
        }
    }

}
