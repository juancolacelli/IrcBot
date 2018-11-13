package com.colacelli.ircbot;

import com.colacelli.ircbot.listeners.OnChannelCommandListener;
import com.colacelli.irclib.actors.User;
import com.colacelli.irclib.connection.Connection;
import com.colacelli.irclib.connection.Server;
import com.colacelli.irclib.connection.listeners.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class IRCBot implements Listenable {
    private final Connection connection = new Connection();
    private ArrayList<Plugin> plugins = new ArrayList<>();
    private HashMap<String, ArrayList<OnChannelCommandListener>> onChannelCommandListeners = new HashMap<>();

    public IRCBot() {
        addOnChannelCommandListener();
    }

    private void addOnChannelCommandListener() {
        addListener((OnChannelMessageListener) (connection, message) -> {
            String[] splittedMessage = message.getText().split(" ");

            if (splittedMessage.length > 0) {
                String command = splittedMessage[0].toUpperCase();

                if (!onChannelCommandListeners.isEmpty()) {
                    String[] args = null;
                    if (splittedMessage.length > 1)
                        args = Arrays.copyOfRange(splittedMessage, 1, splittedMessage.length);
                    String[] finalArgs = args;

                    ArrayList<OnChannelCommandListener> listeners = onChannelCommandListeners.get(command);
                    if (listeners != null) {
                        listeners.forEach((listener) -> listener.onChannelCommand(connection, message, command, finalArgs));
                    }
                }
            }
        });
    }

    public void connect(Server server, User user) {
        connection.connect(server, user);
    }

    public void addPlugin(Plugin plugin) {
        plugin.setup(this);
        plugins.add(plugin);
    }

    public ArrayList<Plugin> getPlugins() {
        return plugins;
    }

    @Override
    public void addListener(OnConnectListener listener) {
        connection.addListener(listener);
    }

    @Override
    public void addListener(OnDisconnectListener listener) {
        connection.addListener(listener);
    }

    @Override
    public void addListener(OnPingListener listener) {
        connection.addListener(listener);
    }

    @Override
    public void addListener(OnJoinListener listener) {
        connection.addListener(listener);
    }

    @Override
    public void addListener(OnPartListener listener) {
        connection.addListener(listener);
    }

    @Override
    public void addListener(OnKickListener listener) {
        connection.addListener(listener);
    }

    @Override
    public void addListener(OnChannelModeListener listener) {
        connection.addListener(listener);
    }

    @Override
    public void addListener(OnChannelMessageListener listener) {
        connection.addListener(listener);
    }

    @Override
    public void addListener(OnPrivateMessageListener listener) {
        connection.addListener(listener);
    }

    @Override
    public void addListener(OnNickChangeListener listener) {
        connection.addListener(listener);
    }

    @Override
    public void addListener(OnCtcpListener listener) {
        connection.addListener(listener);
    }

    @Override
    public void addListener(int rawCode, OnRawCodeListener listener) {
        connection.addListener(rawCode, listener);
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
}