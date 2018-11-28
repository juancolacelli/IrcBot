package com.colacelli.ircbot.plugins.autojoin;

import com.colacelli.ircbot.IRCBot;
import com.colacelli.ircbot.Plugin;
import com.colacelli.irclib.actors.Channel;
import com.colacelli.irclib.connection.listeners.OnConnectListener;

import java.util.ArrayList;

public class AutoJoinPlugin implements Plugin {
    private ArrayList<Channel> channels;
    private OnConnectListener listener;

    public AutoJoinPlugin(ArrayList<Channel> channels) {
        this.channels = channels;

        listener = (connection, server, user) -> channels.forEach((channel) -> connection.join(channel));
    }

    @Override
    public String getName() {
        return "AUTO_JOIN";
    }

    @Override
    public void onLoad(IRCBot bot) {
        bot.addListener(listener);
    }

    @Override
    public void onUnload(IRCBot bot) {
        bot.removeListener(listener);
    }
}
