package com.erplus.sync.entity;

public class DatabaseEntity {
    private final String host;
    private final int port;
    private final String protocol;
    private String database;
    private final String username;
    private final String password;

    public DatabaseEntity(String host, int port, String protocol, String database, String username, String password) {
        this.host = host;
        this.port = port;
        this.protocol = protocol;
        this.database = database;
        this.username = username;
        this.password = password;
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
}
