package com.colacelli.samplebot.plugins.nickserv;

import com.colacelli.ircbot.IRCBot;
import com.colacelli.ircbot.plugins.Plugin;
import com.colacelli.irclib.actors.User;
import com.colacelli.irclib.connection.listeners.OnConnectListener;
import com.colacelli.irclib.messages.PrivateMessage;

public class NickServPlugin implements Plugin {
    private String password;

    public NickServPlugin(String password) {
        this.password = password;
    }

    @Override
    public void setup(IRCBot bot) {
        if (password != null && !password.isEmpty()) {
            bot.addListener((OnConnectListener) (connection, server, user) -> {
                User.Builder userBuilder = new User.Builder();
                userBuilder.setNick("nickserv");

                PrivateMessage.Builder privateMessageBuilder = new PrivateMessage.Builder();
                privateMessageBuilder
                        .setSender(connection.getUser())
                        .setReceiver(userBuilder.build())
                        .setText("identify " + password);

                connection.send(privateMessageBuilder.build());
            });
        }
    }
}