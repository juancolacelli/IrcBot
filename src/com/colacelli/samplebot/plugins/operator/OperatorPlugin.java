package com.colacelli.samplebot.plugins.operator;

import com.colacelli.ircbot.IRCBot;
import com.colacelli.ircbot.plugins.Plugin;
import com.colacelli.irclib.actors.Channel;
import com.colacelli.irclib.connection.listeners.OnJoinListener;
import com.colacelli.irclib.messages.ChannelMessage;

import java.io.IOException;

public class OperatorPlugin implements Plugin {
    @Override
    public void setup(IRCBot bot) {
        bot.addListener((OnJoinListener) (connection, user, channel) -> {
            if (user.getNick() != connection.getUser().getNick()) {
                ChannelMessage.Builder channelMessageBuilder = new ChannelMessage.Builder();
                channelMessageBuilder
                        .setSender(connection.getUser())
                        .setChannel(channel)
                        .setText("Hello " + user.getNick() + " welcome to " + channel.getName());

                if (!user.getNick().equals(connection.getUser().getNick())) {
                    try {
                        connection.send(channelMessageBuilder.build());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        bot.addListener("!op", (connection, message, command, args) -> {
            String nick = message.getSender().getNick();
            if (args != null) nick = args[0];

            try {
                connection.mode(message.getChannel(), "+o " + nick);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        bot.addListener("!deop", (connection, message, command, args) -> {
            String nick = message.getSender().getNick();
            if (args != null) nick = args[0];

            try {
                connection.mode(message.getChannel(), "-o " + nick);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        bot.addListener("!voice", (connection, message, command, args) -> {
            String nick = message.getSender().getNick();
            if (args != null) nick = args[0];

            try {
                connection.mode(message.getChannel(), "+v " + nick);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        bot.addListener("!devoice", (connection, message, command, args) -> {
            String nick = message.getSender().getNick();
            if (args != null) nick = args[0];

            try {
                connection.mode(message.getChannel(), "-v " + nick);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        bot.addListener("!join", (connection, message, command, args) -> {
            if (args != null) {
                String channel = args[0];

                try {
                    connection.join(new Channel(channel));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        bot.addListener("!part", (connection, message, command, args) -> {
            Channel channel = message.getChannel();
            if (args != null) channel = new Channel(args[0]);

            try {
                connection.part(channel);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
