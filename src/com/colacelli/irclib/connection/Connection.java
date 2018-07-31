package com.colacelli.irclib.connection;

import com.colacelli.irclib.actors.Channel;
import com.colacelli.irclib.actors.User;
import com.colacelli.irclib.connection.Rawable.RawCode;
import com.colacelli.irclib.connection.connectors.Connector;
import com.colacelli.irclib.connection.connectors.SecureConnector;
import com.colacelli.irclib.connection.connectors.UnsecureConnector;
import com.colacelli.irclib.connection.listeners.*;
import com.colacelli.irclib.messages.ChannelMessage;
import com.colacelli.irclib.messages.PrivateMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

public final class Connection {
    private Server server;

    private User user;
    private HashMap<String, Channel> channels = new HashMap<>();

    private Connector connector;

    private ArrayList<OnConnectListener> onConnectListeners;
    private ArrayList<OnDisconnectListener> onDisconnectListeners;
    private ArrayList<OnPingListener> onPingListeners;
    private ArrayList<OnJoinListener> onJoinListeners;
    private ArrayList<OnPartListener> onPartListeners;
    private ArrayList<OnKickListener> onKickListeners;
    private ArrayList<OnChannelModeListener> onChannelModeListeners;
    private ArrayList<OnChannelMessageListener> onChannelMessageListeners;
    private HashMap<String, ArrayList<OnChannelCommandListener>> onChannelCommandListeners;
    private ArrayList<OnPrivateMessageListener> onPrivateMessageListeners;
    private ArrayList<OnNickChangeListener> onNickChangeListeners;

    public Connection() {
        onConnectListeners = new ArrayList<>();
        onDisconnectListeners = new ArrayList<>();
        onPingListeners = new ArrayList<>();
        onJoinListeners = new ArrayList<>();
        onPartListeners = new ArrayList<>();
        onKickListeners = new ArrayList<>();
        onChannelModeListeners = new ArrayList<>();
        onChannelMessageListeners = new ArrayList<>();
        onChannelCommandListeners = new HashMap<>();
        onPrivateMessageListeners = new ArrayList<>();
        onNickChangeListeners = new ArrayList<>();

        addListener((OnChannelMessageListener) (connection, message) -> {
            String[] splittedMessage = message.getText().split(" ");
            String command = splittedMessage[0].toUpperCase();

            if (!onChannelCommandListeners.isEmpty()) {
                String[] args = null;
                if (splittedMessage.length > 1) args = Arrays.copyOfRange(splittedMessage, 1, splittedMessage.length);
                String[] finalArgs = args;

                ArrayList<OnChannelCommandListener> listeners = onChannelCommandListeners.get(command);
                if (!listeners.isEmpty()) {
                    listeners.forEach((listener) -> listener.onChannelCommand(connection, message, command, finalArgs));
                }
            }
        });
    }

    public void connect(Server newServer, User newUser) throws IOException {
        try {
            user = newUser;
            server = newServer;

            if (server.isSecure()) {
                connector = new SecureConnector();
            } else {
                connector = new UnsecureConnector();
            }

            connector.connect(server, user);

            if (!server.getPassword().equals("")) {
                connector.send("PASS " + server.getPassword());
            }

            login(user);
        } catch (Exception e) {
            e.printStackTrace();
            reconnect();
        }
    }

    public void disconnect() {
        onDisconnectListeners.forEach((listener) -> listener.onDisconnect(this, server));
    }

    public void join(Channel channel) throws IOException {
        connector.send("JOIN " + channel.getName());

        channels.putIfAbsent(channel.getName(), channel);
    }

    private void listen() throws IOException {
        // Keep reading lines from the server.
        String line;

        while ((line = connector.listen()) != null) {
            System.out.println(line);

            String[] splittedLine = line.split(" ");
            try {
                int rawCode = Integer.parseInt(splittedLine[1]);
                if (rawCode == RawCode.LOGGED_IN.getCode()) {
                    onConnectListeners.forEach((listener) -> listener.onConnect(this, server, user));
                } else if (rawCode == RawCode.NICKNAME_IN_USE.getCode()) {
                    // Re-login with a random ending
                    nick(user.getNick() + (new Random()).nextInt(9));
                }
            } catch (NumberFormatException e) {
                // Not a RAW code
            }

            if (line.toLowerCase().startsWith("ping ")) {
                connector.send("PONG " + line.substring(5));
                onPingListeners.forEach((listener) -> listener.onPing(this));
            } else {
                Channel channel = null;
                User.Builder ircUserBuilder = new User.Builder();

                if (splittedLine[2].contains("#"))
                    channel = channels.get(splittedLine[2]);

                final Channel ircChannel = channel;

                switch (splittedLine[1]) {
                    case "PRIVMSG":
                        int nickIndex = line.indexOf("!");
                        int messageIndex = line.indexOf(":", 1);

                        if (nickIndex != -1 && messageIndex != -1) {
                            String nick = line.substring(1, nickIndex);
                            String login = line.substring(1, line.indexOf("@"));
                            String text = line.substring(messageIndex + 1);

                            ircUserBuilder
                                    .setNick(nick)
                                    .setLogin(login);

                            if (channel != null) {
                                ChannelMessage.Builder ircChannelMessageBuilder = new ChannelMessage.Builder();
                                ircChannelMessageBuilder
                                        .setSender(ircUserBuilder.build())
                                        .setChannel(channel)
                                        .setText(text);

                                onChannelMessageListeners.forEach((listener) -> listener.onChannelMessage(this, ircChannelMessageBuilder.build()));
                            } else {
                                PrivateMessage.Builder ircPrivateMessageBuilder = new PrivateMessage.Builder();
                                ircPrivateMessageBuilder
                                        .setSender(ircUserBuilder.build())
                                        .setReceiver(user)
                                        .setText(text);
                                onPrivateMessageListeners.forEach((listener) -> listener.onPrivateMessage(this, ircPrivateMessageBuilder.build()));
                            }
                        }

                        break;

                    case "JOIN":
                        if (channel != null) {
                            ircUserBuilder.setNick(line.substring(1, line.indexOf("!")));
                            onJoinListeners.forEach((listener) -> listener.onJoin(this, ircUserBuilder.build(), ircChannel));
                        }

                        break;

                    case "KICK":
                        if (channel != null) {
                            ircUserBuilder.setNick(splittedLine[3]);
                            onKickListeners.forEach((listener) -> listener.onKick(this, ircUserBuilder.build(), ircChannel));
                        }

                        break;

                    case "MODE":
                        // FIXME: It just sends the first parameter.
                        if (channel != null) {
                            onChannelModeListeners.forEach((listener) -> listener.onChannelMode(this, ircChannel, splittedLine[3]));
                        }

                        break;

                    case "NICK":
                        String oldNick = line.substring(1, line.indexOf("!"));
                        ircUserBuilder.setNick(oldNick);
                        User nickUser = ircUserBuilder.build();
                        nickUser.setNick(splittedLine[2].substring(1));

                        onNickChangeListeners.forEach((listener) -> listener.onNickChange(this, nickUser));

                        break;
                    case "PART":
                        if (channel != null) {
                            ircUserBuilder.setNick(line.substring(1, line.indexOf("!")));
                            onPartListeners.forEach((listener) -> listener.onPart(this, ircUserBuilder.build(), ircChannel));
                        }

                        break;
                }
            }
        }
    }

    private void login(User user) throws IOException {
        nick(user.getNick());

        connector.send("USER " + user.getLogin() + " 8 * : " + user.getLogin());

        listen();
    }

    public void send(ChannelMessage channelMessage) throws IOException {
        connector.send("PRIVMSG " + channelMessage.getChannel().getName() + " :" + channelMessage.getText());

        channelMessage.setSender(user);
    }

    public void send(PrivateMessage ircPrivateMessage) throws IOException {
        connector.send("PRIVMSG " + ircPrivateMessage.getReceiver().getNick() + " :" + ircPrivateMessage.getText());

        ircPrivateMessage.setSender(user);
    }

    public void mode(Channel channel, String mode) throws IOException {
        connector.send("MODE " + channel.getName() + " " + mode);
    }

    public void nick(String nick) throws IOException {
        user.setNick(nick);

        connector.send("NICK " + nick);
    }

    public void part(Channel channel) throws IOException {
        connector.send("PART " + channel.getName());

        if (channels.get(channel.getName()) != null)
            channels.remove(channel.getName());
    }

    public void reconnect() throws IOException {
        connector.disconnect();
        connector.connect(server, user);
    }

    public User getUser() {
        return user;
    }

    public void addListener(OnConnectListener listener) {
        onConnectListeners.add(listener);
    }

    public void addListener(OnDisconnectListener listener) {
        onDisconnectListeners.add(listener);
    }

    public void addListener(OnPingListener listener) {
        onPingListeners.add(listener);
    }

    public void addListener(OnJoinListener listener) {
        onJoinListeners.add(listener);
    }

    public void addListener(OnPartListener listener) {
        onPartListeners.add(listener);
    }

    public void addListener(OnKickListener listener) {
        onKickListeners.add(listener);
    }

    public void addListener(OnChannelModeListener listener) {
        onChannelModeListeners.add(listener);
    }

    public void addListener(OnChannelMessageListener listener) {
        onChannelMessageListeners.add(listener);
    }

    public void addListener(String command, OnChannelCommandListener listener) {
        command = command.toUpperCase();

        ArrayList<OnChannelCommandListener> currentListeners = onChannelCommandListeners.get(command);

        if (currentListeners == null) {
            currentListeners = new ArrayList<>();
        }

        currentListeners.add(listener);

        onChannelCommandListeners.put(command, currentListeners);
    }

    public void addListener(OnPrivateMessageListener listener) {
        onPrivateMessageListeners.add(listener);
    }

    public void addListener(OnNickChangeListener listener) {
        onNickChangeListeners.add(listener);
    }
}
