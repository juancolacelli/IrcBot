package com.colacelli.samplebot;

import com.colacelli.ircbot.IRCBot;
import com.colacelli.irclib.actors.Channel;
import com.colacelli.irclib.actors.User;
import com.colacelli.irclib.connection.Connection;
import com.colacelli.irclib.connection.Server;
import com.colacelli.irclib.connection.listeners.OnConnectListener;
import com.colacelli.irclib.connection.listeners.OnDisconnectListener;
import com.colacelli.samplebot.plugins.autojoin.AutoJoinPlugin;
import com.colacelli.samplebot.plugins.operator.OperatorPlugin;
import com.colacelli.samplebot.plugins.rejoinonkick.RejoinOnKickPlugin;
import com.colacelli.samplebot.plugins.translator.TranslatorPlugin;

import java.io.IOException;
import java.util.ArrayList;

public class SampleBot {
    public static void main(String[] args) throws Exception {

        Server.Builder serverBuilder = new Server.Builder();
        serverBuilder
                .setHostname(Configurable.SERVER)
                .setPort(Configurable.PORT)
                .setSecure(Configurable.SECURE)
                .setPassword(Configurable.PASSWORD);

        User.Builder userBuilder = new User.Builder();
        userBuilder
                .setNick(Configurable.NICK)
                .setLogin(Configurable.LOGIN);

        IRCBot bot = new IRCBot();

        // FIXME: Support multiple channels
        ArrayList<Channel> channels = new ArrayList<>();
        channels.add(new Channel(Configurable.CHANNEL));

        bot.addPlugin(new OperatorPlugin());
        bot.addPlugin(new AutoJoinPlugin(channels));
        bot.addPlugin(new RejoinOnKickPlugin());
        bot.addPlugin(new TranslatorPlugin());

        bot.connect(
                serverBuilder.build(),
                userBuilder.build()
        );
    }
}
