package com.colacelli.ircbot;

import com.colacelli.irclib.actor.IrcUser;
import com.colacelli.irclib.connection.IrcConnection;
import com.colacelli.irclib.connection.IrcServer;

public class IrcBot {
    public static void main(String[] args) throws Exception {
        IrcConnection ircConnection = new IrcConnection(new IrcConnectionHandlerImplementation());
        ircConnection.connectToServer(
                new IrcServer(Configurable.SERVER, Configurable.PORT, Configurable.SECURE, Configurable.PASSWORD),
                new IrcUser(Configurable.NICK, Configurable.LOGIN)
        );
    }
}