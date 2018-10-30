package com.colacelli.samplebot;

public interface Configurable {
    String SERVER = "irc.kernelpanic.com.ar";
    int PORT = 6667;
    boolean SECURE = false;
    String PASSWORD = "";

    String NICK = "ircbot";
    String LOGIN = "ircbot";
    String[] CHANNELS = {"#debug"};
}
