package com.colacelli.samplebot;

import com.colacelli.ircbot.IRCBot;
import com.colacelli.irclib.actors.Channel;
import com.colacelli.irclib.actors.User;
import com.colacelli.irclib.connection.Server;
import com.colacelli.samplebot.plugins.autojoin.AutoJoinPlugin;
import com.colacelli.samplebot.plugins.help.HelpPlugin;
import com.colacelli.samplebot.plugins.operator.OperatorPlugin;
import com.colacelli.samplebot.plugins.rejoinonkick.RejoinOnKickPlugin;
import com.colacelli.samplebot.plugins.translator.TranslatorPlugin;
import com.colacelli.samplebot.plugins.uptime.UptimePlugin;

import java.util.ArrayList;

public class SampleBot {
    public static void main(String[] args) {

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

        ArrayList<Channel> channels = new ArrayList<>();
        for (String channel : Configurable.CHANNELS) {
            channels.add(new Channel(channel));
        }

        bot.addPlugin(new UptimePlugin());
        bot.addPlugin(new OperatorPlugin());
        bot.addPlugin(new AutoJoinPlugin(channels));
        bot.addPlugin(new RejoinOnKickPlugin());
        bot.addPlugin(new TranslatorPlugin());

        bot.addPlugin(new HelpPlugin());

        bot.connect(
                serverBuilder.build(),
                userBuilder.build()
        );
    }
}
