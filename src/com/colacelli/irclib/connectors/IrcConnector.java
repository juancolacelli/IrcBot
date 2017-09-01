package com.colacelli.irclib.connectors;

import com.colacelli.irclib.IrcServer;
import com.colacelli.irclib.IrcUser;

import java.io.IOException;

public abstract class IrcConnector {
    protected static final String ENTER =  "\r\n";

    public abstract void connect(IrcServer newServer, IrcUser newUser) throws IOException;

    public abstract void disconnect() throws IOException;

    public abstract String listen() throws IOException;

    public abstract void send(String text) throws IOException;
}
