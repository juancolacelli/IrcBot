package com.colacelli.ircbot.plugins.ircop;

import com.colacelli.ircbot.IRCBot;
import com.colacelli.ircbot.Plugin;
import com.colacelli.irclib.connection.listeners.OnConnectListener;

public class IRCopPlugin implements Plugin {
    private String name;
    private String password;
    private OnConnectListener listener;

    public IRCopPlugin(String name, String password) {
        this.name = name;
        this.password = password;
    }

    @Override
    public String getName() {
        return "IRCOP";
    }

    @Override
    public void onLoad(IRCBot bot) {
        listener = (connection, server, user) -> connection.send("OPER " + name + " " + password);
        bot.addListener(listener);
    }

    @Override
    public void onUnload(IRCBot bot) {
        bot.removeListener(listener);
    }
}
