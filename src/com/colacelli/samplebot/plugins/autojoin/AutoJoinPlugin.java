package com.colacelli.samplebot.plugins.autojoin;

import com.colacelli.ircbot.IRCBot;
import com.colacelli.ircbot.plugins.Plugin;
import com.colacelli.irclib.actors.Channel;
import com.colacelli.irclib.connection.listeners.OnConnectListener;

import java.util.ArrayList;

public class AutoJoinPlugin implements Plugin {
    private ArrayList<Channel> channels;

    public AutoJoinPlugin(ArrayList<Channel> channels) {
        this.channels = channels;
    }

    @Override
    public void setup(IRCBot bot) {
        bot.addListener((OnConnectListener) (connection, server, user) -> {
            for (Channel channel : channels) {
                connection.join(channel);
            }
        });
    }
}
