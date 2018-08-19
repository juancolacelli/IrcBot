package com.colacelli.samplebot.plugins.operator;

import com.colacelli.ircbot.IRCBot;
import com.colacelli.ircbot.plugins.Plugin;
import com.colacelli.irclib.actors.Channel;
import com.colacelli.irclib.connection.listeners.*;
import com.colacelli.irclib.messages.ChannelMessage;

import java.io.IOException;

public class OperatorPlugin implements Plugin {
    @Override
    public void setup(IRCBot bot) {
        bot.addListener((OnJoinListener) (connection, user, channel) -> {
            System.out.println(user.getNick() + " joined " + channel.getName());

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
        });

        bot.addListener((OnPartListener) (connection, user, channel) -> System.out.println(user.getNick() + " parted from " + channel.getName()));

        bot.addListener((OnKickListener) (connection, user, channel) -> {
            System.out.println(user.getNick() + " has been kicked from " + channel.getName());

            try {
                // FIXME: Auto-rejoin must be on IRCBot logic
                connection.join(new Channel(channel.getName()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        bot.addListener((OnChannelModeListener) (connection, channel, mode) -> System.out.println("Mode changed to " + mode + " in " + channel.getName()));

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

        bot.addListener((OnPrivateMessageListener) (connection, message) -> System.out.println("Private message received from " + message.getSender().getNick() + ": " + message.getText()));

        bot.addListener((OnNickChangeListener) (connection, user) -> System.out.println(user.getOldNick() + " changed nickname to " + user.getNick()));
    }
}
