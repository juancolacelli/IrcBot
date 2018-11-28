package com.colacelli.ircbot;

import com.colacelli.ircbot.listeners.OnChannelCommandListener;
import com.colacelli.irclib.connection.Connection;
import com.colacelli.irclib.connection.listeners.*;

import java.util.ArrayList;
import java.util.HashMap;

public class IRCBotListener implements Listenable {
    protected final Connection connection;
    protected HashMap<String, ArrayList<OnChannelCommandListener>> onChannelCommandListeners;

    public IRCBotListener() {
        connection = new Connection();
        onChannelCommandListeners = new HashMap<>();
    }

    @Override
    public void addListener(Listener listener) {
        connection.addListener(listener);
    }

    @Override
    public void removeListener(Listener listener) {
        connection.removeListener(listener);
    }

    public void addListener(OnChannelCommandListener listener) {
        String command = listener.channelCommand().toUpperCase();

        ArrayList<OnChannelCommandListener> currentListeners = onChannelCommandListeners.getOrDefault(command, new ArrayList<>());
        currentListeners.add(listener);

        onChannelCommandListeners.put(command, currentListeners);
    }

    public void removeListener(String command) {
        onChannelCommandListeners.get(command.toUpperCase()).clear();
    }
}
