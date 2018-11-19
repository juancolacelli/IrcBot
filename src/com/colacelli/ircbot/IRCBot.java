package com.colacelli.ircbot;

import com.colacelli.ircbot.listeners.OnChannelCommandListener;
import com.colacelli.irclib.actors.User;
import com.colacelli.irclib.connection.Server;
import com.colacelli.irclib.connection.listeners.OnChannelMessageListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class IRCBot extends IRCBotListener {
    private ArrayList<Plugin> plugins;
    private HashMap<String, ArrayList<OnChannelCommandListener>> onChannelCommandListeners;

    public IRCBot() {
        plugins = new ArrayList<>();
        onChannelCommandListeners = new HashMap<>();

        // User Agent used by plugins
        System.setProperty("http.agent", "GNU IRC Bot - https://gitlab.com/jic/ircbot");

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

    public void addListener(String command, OnChannelCommandListener listener) {
        command = command.toUpperCase();

        ArrayList<OnChannelCommandListener> currentListeners = onChannelCommandListeners.get(command);

        if (currentListeners == null) {
            currentListeners = new ArrayList<>();
        }

        currentListeners.add(listener);

        onChannelCommandListeners.put(command, currentListeners);
    }

    public void removeListener(String command, OnChannelCommandListener listener) {
        onChannelCommandListeners.get(command).remove(listener);
    }
}