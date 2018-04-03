package com.colacelli.irclib.connection.listeners;

import com.colacelli.irclib.actors.User;
import com.colacelli.irclib.connection.Connection;
import com.colacelli.irclib.connection.Server;

public interface OnConnectListener {
    void onConnect(Connection connection, Server server, User user);
}
