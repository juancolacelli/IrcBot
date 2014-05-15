package com.colacelli.ircbot;

import com.colacelli.irclib.IrcConnection;

public class IrcBot {
    public static void main(String[] args) throws Exception {
        IrcConnection ircConnection = new IrcConnection(new IrcConnectionHandlerImplementation());
        ircConnection.connect(Configurable.SERVER, Configurable.PORT, Configurable.PASSWORD, Configurable.NICK, Configurable.LOGIN);
    }
}