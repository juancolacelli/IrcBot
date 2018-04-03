package com.colacelli.irclib.connection.listeners;

import com.colacelli.irclib.connection.IrcConnection;
import com.colacelli.irclib.connection.IrcServer;

import java.io.IOException;

public abstract class OnDisconnectListener {
    public abstract void onDisconnect(IrcConnection ircConnection, IrcServer server);
}
