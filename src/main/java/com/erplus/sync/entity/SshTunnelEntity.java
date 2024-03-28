package com.erplus.sync.entity;

public class SshTunnelEntity {

    private final String username;
    private final String jumpHost;
    private final int jumpPort;
    private final String password;

    public SshTunnelEntity(String username, String jumpHost, int jumpPort, String password) {
        this.username = username;
        this.jumpHost = jumpHost;
        this.jumpPort = jumpPort;
        this.password = password;
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
