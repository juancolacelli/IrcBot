package com.colacelli.samplebot;

import com.colacelli.ircbot.IRCBot;
import com.colacelli.irclib.actors.Channel;
import com.colacelli.irclib.actors.User;
import com.colacelli.irclib.connection.Server;
import com.colacelli.irclib.connection.listeners.OnConnectListener;
import com.colacelli.irclib.connection.listeners.OnDisconnectListener;
import com.colacelli.samplebot.plugins.operator.OperatorPlugin;
import com.colacelli.samplebot.plugins.translator.TranslatorPlugin;

import java.io.IOException;

public class SampleBot {
    public static void main(String[] args) throws Exception {

        Server.Builder serverBuilder = new Server.Builder();
        serverBuilder
                .setHostname(Configurable.SERVER)
                .setPort(Configurable.PORT)
                .setSecure(Configurable.SECURE)
                .setPassword(Configurable.PASSWORD);

        User.Builder userBuilder = new User.Builder();
        userBuilder
                .setNick(Configurable.NICK)
                .setLogin(Configurable.LOGIN);

        IRCBot bot = new IRCBot();

        addListeners(bot);

        bot.addPlugin(new TranslatorPlugin());
        bot.addPlugin(new OperatorPlugin());

        bot.connect(
                serverBuilder.build(),
                userBuilder.build()
        );
    }

    private static void addListeners(IRCBot bot) {
        // FIXME: Auto-join channel must be on IRCBot logic
        bot.addListener((OnConnectListener) (connection, server, user) -> {
            System.out.println("Connected to " + server.getHostname() + ":" + server.getPort() + " as: " + user.getNick() + ":" + user.getLogin());

            try {
                connection.join(new Channel(Configurable.CHANNEL));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        bot.addListener((OnDisconnectListener) (connection, server) -> System.out.println("Disconnected from " + server.getHostname() + ":" + server.getPort()));

        bot.addListener(connection -> System.out.println("PING!"));
    }
}
