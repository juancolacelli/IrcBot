package com.colacelli.irclib.connection.listeners;

import com.colacelli.irclib.connection.Connection;

public interface OnServerMessageListener {
    void onServerMessage(Connection connection, String message, String command, String... args);
}
