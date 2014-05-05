package com.colacelli;

import com.colacelli.ircbot.IrcConnection;

public class IrcBot implements Settings {
    public static void main(String[] args) throws Exception {
        IrcConnection ircConnection = new IrcConnection(new IrcConnectionHandlerImplementation());
        ircConnection.connect(SERVER, PORT, PASSWORD, NICK, LOGIN, CHANNEL);
    }
}