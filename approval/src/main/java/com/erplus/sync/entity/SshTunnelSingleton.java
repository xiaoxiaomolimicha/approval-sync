package com.erplus.sync.entity;

import com.erplus.sync.utils.PropertiesSingleton;

import java.util.Properties;

public class SshTunnelSingleton {

    private final String username;
    private final String jumpHost;
    private final int jumpPort;
    private final String password;

    private static volatile SshTunnelSingleton sshTunnelSingleton;

    private SshTunnelSingleton(String username, String jumpHost, int jumpPort, String password) {
        this.username = username;
        this.jumpHost = jumpHost;
        this.jumpPort = jumpPort;
        this.password = password;
    }

    public static SshTunnelSingleton getSshTunnelSingleton() {
        if (sshTunnelSingleton == null) {
            synchronized (SshTunnelSingleton.class) {
                if (sshTunnelSingleton == null) {
                    Properties properties = PropertiesSingleton.getProperties();
                    String host = properties.getProperty("jumpHost");
                    String username = properties.getProperty("jumpUsername");
                    String password = properties.getProperty("jumpPassword");
                    sshTunnelSingleton = new SshTunnelSingleton(username, host, 22, password);
                }
            }
        }
        return sshTunnelSingleton;
    }

    public String getUsername() {
        return username;
    }

    public String getJumpHost() {
        return jumpHost;
    }

    public int getJumpPort() {
        return jumpPort;
    }

    public String getPassword() {
        return password;
    }
}
