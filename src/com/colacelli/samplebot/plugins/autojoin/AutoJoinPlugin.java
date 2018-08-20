package com.colacelli.samplebot.plugins.autojoin;

import com.colacelli.ircbot.IRCBot;
import com.colacelli.ircbot.plugins.Plugin;
import com.colacelli.irclib.actors.Channel;
import com.colacelli.irclib.connection.listeners.OnConnectListener;
import com.colacelli.samplebot.Configurable;

import java.io.IOException;
import java.util.ArrayList;

public class AutoJoinPlugin implements Plugin {
    private ArrayList<Channel> channels;

    public AutoJoinPlugin(ArrayList<Channel> channels) {
        this.channels = channels;
    }

    @Override
    public void setup(IRCBot bot) {
        bot.addListener((OnConnectListener) (connection, server, user) -> {
            System.out.println("Connected to " + server.getHostname() + ":" + server.getPort() + " as: " + user.getNick() + ":" + user.getLogin());

            try {
                for(Channel channel : channels) {
                    connection.join(channel);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });


    }
}
