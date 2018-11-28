package com.colacelli.ircbot.plugins.rejoinonkick;

import com.colacelli.ircbot.IRCBot;
import com.colacelli.ircbot.Plugin;
import com.colacelli.irclib.actors.Channel;
import com.colacelli.irclib.actors.User;
import com.colacelli.irclib.connection.Connection;
import com.colacelli.irclib.connection.Rawable;
import com.colacelli.irclib.connection.listeners.Listener;
import com.colacelli.irclib.connection.listeners.OnKickListener;
import com.colacelli.irclib.connection.listeners.OnRawCodeListener;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class RejoinOnKickPlugin implements Plugin {
    private ArrayList<Listener> listeners;

    public RejoinOnKickPlugin() {
        listeners = new ArrayList<>();

        listeners.add((OnKickListener) (connection, user, channel) -> {
            if (user.getNick().equals(connection.getUser().getNick())) connection.join(new Channel(channel.getName()));
        });

        listeners.add(new OnRawCodeListener() {
            @Override
            public int rawCode() {
                return Rawable.RawCode.JOIN_BANNED.getCode();
            }

            @Override
            public void onRawCode(Connection connection, String message, int rawCode, String... args) {
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        connection.join(new Channel(args[3]));
                    }
                }, 5000);
            }
        });
    }

    @Override
    public String name() {
        return "REJOIN_ON_KICK";
    }

    @Override
    public void onLoad(IRCBot bot) {
        listeners.forEach((listener) -> bot.addListener(listener));
    }

    @Override
    public void onUnload(IRCBot bot) {
        listeners.forEach((listener) -> bot.removeListener(listener));
    }
}
