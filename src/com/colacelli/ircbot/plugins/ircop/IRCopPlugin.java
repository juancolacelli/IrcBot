package com.colacelli.ircbot.plugins.ircop;

import com.colacelli.ircbot.IRCBot;
import com.colacelli.ircbot.Plugin;
import com.colacelli.irclib.connection.listeners.OnConnectListener;

public class IRCopPlugin implements Plugin {
    private String name;
    private String password;

    public IRCopPlugin(String name, String password) {
        this.name = name;
        this.password = password;
    }

    @Override
    public void setup(IRCBot bot) {
        bot.addListener((OnConnectListener) (connection, server, user) -> connection.send("OPER " + name + " " + password));
    }
}
