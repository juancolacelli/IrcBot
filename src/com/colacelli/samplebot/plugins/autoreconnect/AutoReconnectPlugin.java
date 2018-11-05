package com.colacelli.samplebot.plugins.autoreconnect;

import com.colacelli.ircbot.IRCBot;
import com.colacelli.ircbot.plugins.Plugin;
import com.colacelli.irclib.connection.listeners.OnDisconnectListener;

public class AutoReconnectPlugin implements Plugin {
    @Override
    public void setup(IRCBot bot) {
        bot.addListener((OnDisconnectListener) (connection, server) -> {
            bot.connect(server, connection.getUser());
        });
    }
}
