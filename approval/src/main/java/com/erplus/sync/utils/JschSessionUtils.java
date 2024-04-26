package com.erplus.sync.utils;

import com.erplus.sync.mybatis.MybatisManager;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.erplus.sync.entity.SshTunnelSingleton;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JschSessionUtils {

    private static volatile Session jschSession;

    public static Session getJschSession() throws Throwable{
        if (jschSession == null) {
            synchronized (JschSessionUtils.class) {
                if (jschSession == null) {
                    SshTunnelSingleton sshTunnelSingleton = SshTunnelSingleton.getSshTunnelSingleton();
                    JSch jSch = new JSch();
                    jschSession = jSch.getSession(sshTunnelSingleton.getUsername(), sshTunnelSingleton.getJumpHost(), sshTunnelSingleton.getJumpPort());
                    jschSession.setPassword(sshTunnelSingleton.getPassword());
                    jschSession.setConfig("StrictHostKeyChecking", "no");
                    jschSession.setServerAliveInterval(60000); // 设置心跳间隔为60秒
                    jschSession.setServerAliveCountMax(3);
                    jschSession.connect();
                }
            }
        }
        return jschSession;
    }


    public static void closeSession() {
        if (jschSession != null) {
            try {
                for (int port : ForwardPortUtils.localPorts) {
                    log.info("我关闭了:{}端口的映射", port);
                    jschSession.delPortForwardingL(port);
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            } finally {
                log.info("我关闭了:jsch的Session连接");
                jschSession.disconnect();
            }
        }
    }

    public static void closeSession(int ...localPort) {
        if (jschSession != null) {
            try {
                for (int port : localPort) {
                    jschSession.delPortForwardingL(port);
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            } finally {
                jschSession.disconnect();
            }
        }
    }

    public static void closeAll() {
        MysqlConnectionUtils.closeConnection();
        EsClientUtils.closeClient();
        MybatisManager.closeSqlSession();
        DataSourceSingleton.closeDataSource();
        JschSessionUtils.closeSession();
    }

}
