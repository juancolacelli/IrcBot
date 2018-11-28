package com.colacelli.ircbot;

import com.colacelli.ircbot.listeners.OnChannelCommandListener;
import com.colacelli.irclib.actors.User;
import com.colacelli.irclib.connection.Server;
import com.colacelli.irclib.connection.listeners.OnChannelMessageListener;

import java.util.ArrayList;
import java.util.Arrays;

public class IRCBot extends IRCBotListener {
    private static final String HTTP_USER_AGENT = "GNU IRC Bot - https://gitlab.com/jic/ircbot";
    private ArrayList<Plugin> plugins;

    public IRCBot() {
        plugins = new ArrayList<>();

        // User Agent used by plugins
        System.setProperty("http.agent", HTTP_USER_AGENT);

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
        plugin.onLoad(this);
        plugins.add(plugin);
    }

    public void removePlugin(Plugin plugin) {
        plugin.onUnload(this);
        plugins.remove(plugin);
    }

    public ArrayList<Plugin> getPlugins() {
        return plugins;
    }
}