package com.colacelli.irclib.connection.listeners;

import com.colacelli.irclib.connection.IrcConnection;
import com.colacelli.irclib.connection.IrcServer;

public interface OnDisconnectListener {
    void onDisconnect(IrcConnection ircConnection, IrcServer server);
}
