package com.colacelli.samplebot;

public interface Configurable {
    String SERVER = "irc.kernelpanic.com.ar";
    int PORT = 6697;
    boolean SECURE = true;
    String PASSWORD = "";

    String NICK = "ircbot";
    String LOGIN = "ircbot";
    String[] CHANNELS = {"#debug"};
}
