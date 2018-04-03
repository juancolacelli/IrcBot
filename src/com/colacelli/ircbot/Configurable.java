package com.colacelli.ircbot;

public interface Configurable {
    String SERVER = "irc.freenode.net";
    int PORT = 6697;
    boolean SECURE = true;
    String PASSWORD = "";

    String NICK = "IrcBot";
    String LOGIN = "IrcBot";
    String CHANNEL = "#debug";
}
