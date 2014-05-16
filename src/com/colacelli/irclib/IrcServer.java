package com.colacelli.irclib;

public class IrcServer {
    private String hostname;
    private int port;
    private String password;
    
    public IrcServer(String hostname, int port, String password) {
        this.hostname = hostname;
        this.port     = port;
        this.password = password;
    }
    
    public String getHostname() {
        return this.hostname;
    }
    
    public int getPort() {
        return this.port;
    }
    
    public String getPassword() {
        return this.password;
    }
}
