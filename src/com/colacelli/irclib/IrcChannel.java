package com.colacelli.irclib;

public class IrcChannel {
    private String name;
    
    IrcChannel(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
}
