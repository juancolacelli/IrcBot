package com.colacelli.ircbot;

public interface Plugin {
    String name();
    void onLoad(IRCBot bot);
    void onUnload(IRCBot bot);
}
