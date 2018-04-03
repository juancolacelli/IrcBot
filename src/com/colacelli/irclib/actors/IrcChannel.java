package com.colacelli.irclib.actors;

public class IrcChannel {
    private String name;

    public IrcChannel(String newName) {
        name = newName;
    }

    public String getName() {
        return name;
    }
}
