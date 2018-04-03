package com.colacelli.irclib.connection;

import com.colacelli.irclib.actors.IrcChannel;
import com.colacelli.irclib.actors.IrcUser;
import com.colacelli.irclib.connection.Rawable.RawCode;
import com.colacelli.irclib.connection.connectors.IrcConnector;
import com.colacelli.irclib.connection.connectors.IrcSecureConnector;
import com.colacelli.irclib.connection.connectors.IrcUnsecureConnector;
import com.colacelli.irclib.connection.listeners.*;
import com.colacelli.irclib.messages.IrcChannelMessage;
import com.colacelli.irclib.messages.IrcPrivateMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public final class IrcConnection {
    private IrcServer currentServer;

    private IrcUser currentUser;
    private HashMap<String, IrcChannel> channelsJoined = new HashMap<>();

    private IrcConnector ircConnector;

    private ArrayList<OnConnectListener> onConnectListeners;
    private ArrayList<OnDisconnectListener> onDisconnectListeners;
    private ArrayList<OnPingListener> onPingListeners;
    private ArrayList<OnJoinListener> onJoinListeners;
    private ArrayList<OnPartListener> onPartListeners;
    private ArrayList<OnKickListener> onKickListeners;
    private ArrayList<OnChannelModeListener> onChannelModeListeners;
    private ArrayList<OnChannelMessageListener> onChannelMessageListeners;
    private ArrayList<OnPrivateMessageListener> onPrivateMessageListeners;
    private ArrayList<OnNickChangeListener> onNickChangeListeners;

    public IrcConnection() {
        onConnectListeners = new ArrayList<>();
        onDisconnectListeners = new ArrayList<>();
        onPingListeners = new ArrayList<>();
        onJoinListeners = new ArrayList<>();
        onPartListeners = new ArrayList<>();
        onKickListeners = new ArrayList<>();
        onChannelModeListeners = new ArrayList<>();
        onChannelMessageListeners = new ArrayList<>();
        onPrivateMessageListeners = new ArrayList<>();
        onNickChangeListeners = new ArrayList<>();
    }

    public void connect(IrcServer newServer, IrcUser newUser) throws IOException {
        try {
            currentUser = newUser;
            currentServer = newServer;

            if (currentServer.isSecure()) {
                ircConnector = new IrcSecureConnector();
            } else {
                ircConnector = new IrcUnsecureConnector();
            }

            ircConnector.connect(currentServer, currentUser);

            if (!currentServer.getPassword().equals("")) {
                ircConnector.send("PASS " + currentServer.getPassword());
            }

            loginToServer(currentUser);
        } catch (Exception e) {
            e.printStackTrace();
            reconnect();
        }
    }

    public void disconnect() {
        onDisconnectListeners.forEach((listener) -> listener.onDisconnect(this, currentServer));
    }

    public void join(IrcChannel channel) throws IOException {
        ircConnector.send("JOIN " + channel.getName());

        channelsJoined.putIfAbsent(channel.getName(), channel);
    }

    private void listen() throws IOException {
        // Keep reading lines from the server.
        String line;

        while ((line = ircConnector.listen()) != null) {
            System.out.println(line);

            String[] splittedLine = line.split(" ");
            try {
                int rawCode = Integer.parseInt(splittedLine[1]);
                if (rawCode == RawCode.LOGGED_IN.getCode()) {
                    onConnectListeners.forEach((listener) -> listener.onConnect(this, currentServer, currentUser));
                } else if (rawCode == RawCode.NICKNAME_IN_USE.getCode()) {
                    // Re-login with a random ending
                    nick(currentUser.getNick() + (new Random()).nextInt(9));
                }
            } catch (NumberFormatException e) {
                // Not a RAW code
            }

            if (line.toLowerCase().startsWith("ping ")) {
                ircConnector.send("PONG " + line.substring(5));
                onPingListeners.forEach((listener) -> listener.onPing(this));
            } else {
                IrcChannel channel = null;
                IrcUser.Builder ircUserBuilder = new IrcUser.Builder();

                if (splittedLine[2].contains("#"))
                    channel = channelsJoined.get(splittedLine[2]);

                final IrcChannel ircChannel = channel;

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
                                IrcChannelMessage.Builder ircChannelMessageBuilder = new IrcChannelMessage.Builder();
                                ircChannelMessageBuilder
                                        .setSender(ircUserBuilder.build())
                                        .setChannel(channel)
                                        .setText(text);

                                onChannelMessageListeners.forEach((listener) -> listener.onChannelMessage(this, ircChannelMessageBuilder.build()));
                            } else {
                                IrcPrivateMessage.Builder ircPrivateMessageBuilder = new IrcPrivateMessage.Builder();
                                ircPrivateMessageBuilder
                                        .setSender(ircUserBuilder.build())
                                        .setReceiver(currentUser)
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
                        IrcUser nickUser = ircUserBuilder.build();
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

    private void loginToServer(IrcUser user) throws IOException {
        nick(user.getNick());

        ircConnector.send("USER " + user.getLogin() + " 8 * : " + user.getLogin());

        listen();
    }

    public void send(IrcChannelMessage ircChannelMessage) throws IOException {
        ircConnector.send("PRIVMSG " + ircChannelMessage.getChannel().getName() + " :" + ircChannelMessage.getText());

        ircChannelMessage.setSender(currentUser);
    }

    public void send(IrcPrivateMessage ircPrivateMessage) throws IOException {
        ircConnector.send("PRIVMSG " + ircPrivateMessage.getReceiver().getNick() + " :" + ircPrivateMessage.getText());

        ircPrivateMessage.setSender(currentUser);
    }

    public void mode(IrcChannel channel, String mode) throws IOException {
        ircConnector.send("MODE " + channel.getName() + " " + mode);
    }

    public void nick(String nick) throws IOException {
        currentUser.setNick(nick);

        ircConnector.send("NICK " + nick);
    }

    public void part(IrcChannel channel) throws IOException {
        ircConnector.send("PART " + channel.getName());

        if (channelsJoined.get(channel.getName()) != null)
            channelsJoined.remove(channel.getName());
    }

    public void reconnect() throws IOException {
        ircConnector.disconnect();
        ircConnector.connect(currentServer, currentUser);
    }

    public IrcUser getCurrentUser() {
        return currentUser;
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

    public void addListener(OnPrivateMessageListener listener) {
        onPrivateMessageListeners.add(listener);
    }

    public void addListener(OnNickChangeListener listener) {
        onNickChangeListeners.add(listener);
    }
}
