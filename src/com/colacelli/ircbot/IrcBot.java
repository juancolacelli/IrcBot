package com.colacelli.ircbot;

import com.colacelli.irclib.IrcConnection;
import com.colacelli.irclib.IrcServer;

public class IrcBot {
    public static void main(String[] args) throws Exception {
        IrcConnection ircConnection = new IrcConnection(new IrcConnectionHandlerImplementation());
        ircConnection.connect(new IrcServer(Configurable.SERVER, Configurable.PORT, Configurable.PASSWORD), Configurable.NICK, Configurable.LOGIN);
    }
}