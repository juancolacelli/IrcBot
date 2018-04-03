package com.colacelli.irclib.connection.connectors;

import com.colacelli.irclib.actors.User;
import com.colacelli.irclib.connection.Server;

import java.io.IOException;

public abstract class Connector {
    protected static final String ENTER = "\r\n";

    public abstract void connect(Server newServer, User newUser) throws IOException;

    public abstract void disconnect() throws IOException;

    public abstract String listen() throws IOException;

    public abstract void send(String text) throws IOException;
}
