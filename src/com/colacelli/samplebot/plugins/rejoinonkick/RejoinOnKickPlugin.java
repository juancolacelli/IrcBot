package com.colacelli.samplebot.plugins.rejoinonkick;

import com.colacelli.ircbot.IRCBot;
import com.colacelli.ircbot.plugins.Plugin;
import com.colacelli.irclib.actors.Channel;
import com.colacelli.irclib.connection.listeners.OnKickListener;

public class RejoinOnKickPlugin implements Plugin {
    @Override
    public void setup(IRCBot bot) {
        bot.addListener((OnKickListener) (connection, user, channel) -> {
            if (user.getNick().equals(connection.getUser().getNick())) connection.join(new Channel(channel.getName()));
        });
    }
}
