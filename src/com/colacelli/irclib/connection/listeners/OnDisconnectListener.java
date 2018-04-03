package com.colacelli.irclib.connection.listeners;

import com.colacelli.irclib.connection.Connection;
import com.colacelli.irclib.connection.Server;

public interface OnDisconnectListener {
    void onDisconnect(Connection connection, Server server);
}
