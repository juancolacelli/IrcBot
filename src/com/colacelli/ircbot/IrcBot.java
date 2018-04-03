package com.colacelli.ircbot;

import com.colacelli.irclib.actor.IrcUser;
import com.colacelli.irclib.connection.IrcConnection;
import com.colacelli.irclib.connection.IrcServer;

public class IrcBot {
    public static void main(String[] args) throws Exception {
        IrcConnection ircConnection = new IrcConnection(new IrcConnectionHandlerImplementation());

        IrcServer.Builder ircServerBuilder = new IrcServer.Builder();
        ircServerBuilder
                .setHostname(Configurable.SERVER)
                .setPort(Configurable.PORT)
                .setSecure(Configurable.SECURE)
                .setPassword(Configurable.PASSWORD);

        IrcUser.Builder ircUserBuilder = new IrcUser.Builder();
        ircUserBuilder
                .setNick(Configurable.NICK)
                .setLogin(Configurable.LOGIN);

        ircConnection.connectToServer(
                ircServerBuilder.build(),
                ircUserBuilder.build()
        );
    }
}