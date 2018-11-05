package com.colacelli.samplebot.plugins.rejoinonkick;

import com.colacelli.ircbot.IRCBot;
import com.colacelli.ircbot.plugins.Plugin;
import com.colacelli.irclib.actors.Channel;
import com.colacelli.irclib.connection.Rawable;
import com.colacelli.irclib.connection.listeners.OnKickListener;

import java.util.Timer;
import java.util.TimerTask;

public class RejoinOnKickPlugin implements Plugin {
    @Override
    public void setup(IRCBot bot) {
        bot.addListener((OnKickListener) (connection, user, channel) -> {
            if (user.getNick().equals(connection.getUser().getNick())) connection.join(new Channel(channel.getName()));
        });

        bot.addListener(Rawable.RawCode.JOIN_BANNED.getCode(), (connection, message, rawCode, args) -> {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    connection.join(new Channel(args[3]));
                }
            }, 5000);
        });
    }
}
