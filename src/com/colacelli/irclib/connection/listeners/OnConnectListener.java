package com.colacelli.irclib.connection.listeners;

import com.colacelli.irclib.actor.IrcUser;
import com.colacelli.irclib.connection.IrcConnection;
import com.colacelli.irclib.connection.IrcServer;

import java.io.IOException;

public abstract class OnConnectListener {
    public abstract void onConnect(IrcConnection ircConnection, IrcServer server, IrcUser user);
}
