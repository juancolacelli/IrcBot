package com.colacelli.ircbot;

import com.colacelli.irclib.IrcConnection;
import com.colacelli.irclib.IrcServer;
import com.colacelli.irclib.IrcUser;

public class IrcBot {
    public static void main(String[] args) throws Exception {
        IrcConnection ircConnection = new IrcConnection(new IrcConnectionHandlerImplementation());
        ircConnection.connectToServer(new IrcServer(Configurable.SERVER, Configurable.PORT, Configurable.PASSWORD), new IrcUser(Configurable.NICK, Configurable.LOGIN));
    }
}