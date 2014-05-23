package com.colacelli.irclib;

public class IrcChannel {
    private String name;
    
    public IrcChannel(String newName) {
        name = newName;
    }
    
    public String getName() {
        return name;
    }
}
