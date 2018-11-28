package com.colacelli.ircbot;

public interface Plugin {
    String getName();

    void onLoad(IRCBot bot);

    void onUnload(IRCBot bot);
}
