package com.colacelli.ircbot;

import com.colacelli.irclib.actors.Channel;
import com.colacelli.irclib.actors.User;
import com.colacelli.irclib.connection.Connection;
import com.colacelli.irclib.connection.Server;
import com.colacelli.irclib.connection.listeners.*;
import com.colacelli.irclib.messages.ChannelMessage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class IRCBot {
    static Connection connection = new Connection();
    static EsperantoTranslator esperantoTranslator = new EsperantoTranslator();

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

        addListeners();

        connection.connect(
                serverBuilder.build(),
                userBuilder.build()
        );
    }

    private static void addListeners() {
        connection.addListener((OnConnectListener) (connection, server, user) -> {
            System.out.println("Connected to " + server.getHostname() + ":" + server.getPort() + " as: " + user.getNick() + ":" + user.getLogin());

            try {
                connection.join(new Channel(Configurable.CHANNEL));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        connection.addListener((OnDisconnectListener) (connection, server) -> System.out.println("Disconnected from " + server.getHostname() + ":" + server.getPort()));

        connection.addListener(connection -> System.out.println("PING!"));

        connection.addListener((OnJoinListener) (connection, user, channel) -> {
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

        connection.addListener((OnPartListener) (connection, user, channel) -> System.out.println(user.getNick() + " parted from " + channel.getName()));

        connection.addListener((OnKickListener) (connection, user, channel) -> {
            System.out.println(user.getNick() + " has been kicked from " + channel.getName());

            try {
                connection.join(new Channel(Configurable.CHANNEL));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        connection.addListener((OnChannelModeListener) (connection, channel, mode) -> System.out.println("Mode changed to " + mode + " in " + channel.getName()));

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

        connection.addListener("!eo", (connection, message, command, args) -> {
            String word = EsperantoTranslator.purgeWord(args[0]);
            HashMap<String, String> translations = esperantoTranslator.translate(word);

            if (!translations.isEmpty()) {
                for(Map.Entry<String, String> entry : translations.entrySet()) {
                    word = entry.getKey();
                    String translation = entry.getValue();

                    ChannelMessage.Builder channelMessageBuilder = new ChannelMessage.Builder();
                    channelMessageBuilder
                            .setSender(connection.getUser())
                            .setChannel(message.getChannel())
                            .setText(word + ": " + translation);

                    try {
                        connection.send(channelMessageBuilder.build());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        connection.addListener((OnPrivateMessageListener) (connection, message) -> System.out.println("Private message received from " + message.getSender().getNick() + ": " + message.getText()));

        connection.addListener((OnNickChangeListener) (connection, user) -> System.out.println(user.getOldNick() + " changed nickname to " + user.getNick()));
    }
}