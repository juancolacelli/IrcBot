package com.colacelli.ircbot;

import com.colacelli.irclib.IrcConnection;

public class IrcBot {
    public static void main(String[] args) throws Exception {
        IrcConnection ircConnection = new IrcConnection(new IrcConnectionHandlerImplementation());
        ircConnection.connectToServer(Configurable.SERVER, Configurable.PORT, Configurable.PASSWORD, Configurable.NICK, Configurable.LOGIN);
    }
}