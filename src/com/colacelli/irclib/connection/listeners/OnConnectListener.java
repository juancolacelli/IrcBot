package com.colacelli.irclib.connection.listeners;

import com.colacelli.irclib.actors.IrcUser;
import com.colacelli.irclib.connection.IrcConnection;
import com.colacelli.irclib.connection.IrcServer;

public interface OnConnectListener {
    void onConnect(IrcConnection ircConnection, IrcServer server, IrcUser user);
}
