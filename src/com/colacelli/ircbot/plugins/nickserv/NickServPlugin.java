package com.colacelli.ircbot.plugins.nickserv;

import com.colacelli.ircbot.IRCBot;
import com.colacelli.ircbot.Plugin;
import com.colacelli.irclib.actors.User;
import com.colacelli.irclib.connection.listeners.OnConnectListener;
import com.colacelli.irclib.messages.PrivateMessage;

public class NickServPlugin implements Plugin {
    private String password;
    private OnConnectListener listener;

    public NickServPlugin(String password) {
        this.password = password;

        listener = (connection, server, user) -> {
            User.Builder userBuilder = new User.Builder();
            userBuilder.setNick("nickserv");

            PrivateMessage.Builder builder = new PrivateMessage.Builder();
            builder
                    .setSender(connection.getUser())
                    .setReceiver(userBuilder.build())
                    .setText("identify " + password);

            connection.send(builder.build());
        };
    }

    @Override
    public String getName() {
        return "NICKSERV";
    }

    @Override
    public void onLoad(IRCBot bot) {
        if (password != null && !password.isEmpty()) {
            bot.addListener(listener);
        }
    }

    @Override
    public void onUnload(IRCBot bot) {
        bot.removeListener(listener);
    }
}
