package com.colacelli.ircbot;

import com.colacelli.irclib.actors.Channel;
import com.colacelli.irclib.actors.User;
import com.colacelli.irclib.connection.Connection;
import com.colacelli.irclib.connection.Server;
import com.colacelli.irclib.connection.listeners.*;
import com.colacelli.irclib.messages.ChannelMessage;
import com.colacelli.irclib.messages.PrivateMessage;

import java.io.IOException;
import java.util.Arrays;

public class IRCBot {
    static Connection connection = new Connection();

    public static void main(String[] args) throws Exception {

        Server.Builder ircServerBuilder = new Server.Builder();
        ircServerBuilder
                .setHostname(Configurable.SERVER)
                .setPort(Configurable.PORT)
                .setSecure(Configurable.SECURE)
                .setPassword(Configurable.PASSWORD);

        User.Builder ircUserBuilder = new User.Builder();
        ircUserBuilder
                .setNick(Configurable.NICK)
                .setLogin(Configurable.LOGIN);

        addListeners();

        connection.connect(
                ircServerBuilder.build(),
                ircUserBuilder.build()
        );
    }

    private static void addListeners() {
        connection.addListener((OnConnectListener) (ircConnection, server, user) -> {
            System.out.println("Connected to " + server.getHostname() + ":" + server.getPort() + " as: " + user.getNick() + ":" + user.getLogin());

            try {
                ircConnection.join(new Channel(Configurable.CHANNEL));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        connection.addListener((OnDisconnectListener) (ircConnection, server) -> System.out.println("Disconnected from " + server.getHostname() + ":" + server.getPort()));

        connection.addListener(ircConnection -> System.out.println("PING!"));

        connection.addListener((OnJoinListener) (ircConnection, user, channel) -> {
            System.out.println(user.getNick() + " joined " + channel.getName());

            ChannelMessage.Builder ircChannelMessageBuilder = new ChannelMessage.Builder();
            ircChannelMessageBuilder
                    .setSender(ircConnection.getUser())
                    .setChannel(channel)
                    .setText("Hello " + user.getNick() + " welcome to " + channel.getName());

            if (!user.getNick().equals(ircConnection.getUser().getNick())) {
                try {
                    ircConnection.send(ircChannelMessageBuilder.build());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        connection.addListener((OnPartListener) (ircConnection, user, channel) -> System.out.println(user.getNick() + " parted from " + channel.getName()));

        connection.addListener((OnKickListener) (ircConnection, user, channel) -> {
            System.out.println(user.getNick() + " has been kicked from " + channel.getName());

            try {
                ircConnection.join(new Channel(Configurable.CHANNEL));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        connection.addListener((OnChannelModeListener) (ircConnection, channel, mode) -> System.out.println("Mode changed to " + mode + " in " + channel.getName()));

        connection.addListener("!op", (connection, message, command, args) -> {
            String nick = message.getSender().getNick();
            if (args != null) nick = args[0];

            try {
                connection.mode(message.getChannel(), "+o " + nick);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        connection.addListener("!deop", (connection, message, command, args) -> {
            String nick = message.getSender().getNick();
            if (args != null) nick = args[0];

            try {
                connection.mode(message.getChannel(), "-o " + nick);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        connection.addListener("!voice", (connection, message, command, args) -> {
            String nick = message.getSender().getNick();
            if (args != null) nick = args[0];

            try {
                connection.mode(message.getChannel(), "+v " + nick);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        connection.addListener("!devoice", (connection, message, command, args) -> {
            String nick = message.getSender().getNick();
            if (args != null) nick = args[0];

            try {
                connection.mode(message.getChannel(), "-v " + nick);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        connection.addListener("!join", (connection, message, command, args) -> {
            if (args != null) {
                String channel = args[0];

                try {
                    connection.join(new Channel(channel));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        connection.addListener("!part", (connection, message, command, args) -> {
            Channel channel = message.getChannel();
            if (args != null) channel = new Channel(args[0]);

            try {
                connection.part(channel);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        connection.addListener((OnPrivateMessageListener) (ircConnection, message) -> System.out.println("Private message received from " + message.getSender().getNick() + ": " + message.getText()));

        connection.addListener((OnNickChangeListener) (ircConnection, user) -> System.out.println(user.getOldNick() + " changed nickname to " + user.getNick()));
    }
}