package com.colacelli.ircbot.plugins.autoreconnect;

import com.colacelli.ircbot.IRCBot;
import com.colacelli.ircbot.Plugin;
import com.colacelli.irclib.connection.listeners.OnDisconnectListener;

public class AutoReconnectPlugin implements Plugin {
    private OnDisconnectListener listener;

    @Override
    public String getName() {
        return "AUTO_RECONNECT";
    }

    @Override
    public void onLoad(IRCBot bot) {
        listener = (connection, server) -> bot.connect(server, connection.getUser());
        bot.addListener(listener);
    }

    @Override
    public void onUnload(IRCBot bot) {
        bot.removeListener(listener);
    }
}
