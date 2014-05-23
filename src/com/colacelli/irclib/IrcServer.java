package com.colacelli.irclib;

public class IrcServer {
    private String hostname;
    private int port;
    private String password;
    
    public IrcServer(String newHostname, int newPort, String newPassword) {
        hostname = newHostname;
        port     = newPort;
        password = newPassword;
    }
    
    public String getHostname() {
        return hostname;
    }
    
    public int getPort() {
        return port;
    }
    
    public String getPassword() {
        return password;
    }
}
