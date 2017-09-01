package com.colacelli.irclib;

public class IrcServer {
    private String hostname;
    private int port;
    private boolean secure;
    private String password;
    
    public IrcServer(String newHostname, int newPort, boolean newSecure, String newPassword) {
        hostname = newHostname;
        port     = newPort;
        secure   = newSecure;
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

    public boolean isSecure() {
        return secure;
    }
}
