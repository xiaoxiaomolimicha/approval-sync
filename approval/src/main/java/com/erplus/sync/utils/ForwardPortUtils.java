package com.erplus.sync.utils;


import com.erplus.sync.entity.DatabaseSingleton;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;

import java.net.BindException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Slf4j
public class ForwardPortUtils {

    public static final List<Integer> localPorts = new ArrayList<>();

    public static void forwardMysqlPort() {
        try {
            // 创建端口转发通道
            DatabaseSingleton databaseSingleton = DatabaseSingleton.getDatabaseSingleton();
            Session jschSession = JschSessionUtils.getJschSession();
            jschSession.setPortForwardingL(MysqlConnectionUtils.MYSQL_LOCAL_PORT, databaseSingleton.getHost(), databaseSingleton.getPort());
            localPorts.add(MysqlConnectionUtils.MYSQL_LOCAL_PORT);
        } catch (JSchException ignored) {
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
        }
    }

    public static void forwardEsPort() {
        try {
            Properties properties = PropertiesSingleton.getProperties();
            String esHost = properties.getProperty("esHost");
            String esPort = properties.getProperty("esPort");
            Session jschSession = JschSessionUtils.getJschSession();
            // 设置端口转发
            jschSession.setPortForwardingL(EsClientUtils.ES_LOCAL_PORT, esHost, Integer.parseInt(esPort));
            localPorts.add(EsClientUtils.ES_LOCAL_PORT);
        } catch (Throwable e) {
            log.error(e.getMessage());
        }
    }


}
