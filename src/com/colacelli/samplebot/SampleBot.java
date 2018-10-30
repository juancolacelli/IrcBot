package com.colacelli.samplebot;

import com.colacelli.ircbot.IRCBot;
import com.colacelli.irclib.actors.Channel;
import com.colacelli.irclib.actors.User;
import com.colacelli.irclib.connection.Server;
import com.colacelli.samplebot.plugins.autojoin.AutoJoinPlugin;
import com.colacelli.samplebot.plugins.help.HelpPlugin;
import com.colacelli.samplebot.plugins.nickserv.NickServPlugin;
import com.colacelli.samplebot.plugins.operator.OperatorPlugin;
import com.colacelli.samplebot.plugins.rejoinonkick.RejoinOnKickPlugin;
import com.colacelli.samplebot.plugins.translator.TranslatorPlugin;
import com.colacelli.samplebot.plugins.uptime.UptimePlugin;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

public class SampleBot {
    private static final String PROPERTIES_FILE = "com/colacelli/samplebot/samplebot.properties";

    public static void main(String[] args) {
        Properties properties = loadProperties(PROPERTIES_FILE);

        IRCBot bot = new IRCBot();

        addPlugins(bot, properties);

        bot.connect(
                buildServer(properties),
                buildUser(properties)
        );
    }

    private static Properties loadProperties(String propertiesFile) {
        InputStream inputStream;
        inputStream = ClassLoader.getSystemResourceAsStream(propertiesFile);

        Properties properties = new Properties();
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return properties;
    }

    private static User buildUser(Properties properties) {
        User.Builder userBuilder = new User.Builder();
        userBuilder
                .setNick(properties.getProperty("NICK"))
                .setLogin(properties.getProperty("LOGIN"));

        return userBuilder.build();
    }

    private static Server buildServer(Properties properties) {
        Server.Builder serverBuilder = new Server.Builder();
        serverBuilder
                .setHostname(properties.getProperty("SERVER"))
                .setPort(Integer.parseInt(properties.getProperty("PORT")))
                .setSecure(Boolean.parseBoolean(properties.getProperty("SECURE")))
                .setPassword(properties.getProperty("PASSWORD"));

        return serverBuilder.build();
    }

    private static void addPlugins(IRCBot bot, Properties properties) {
        ArrayList<Channel> channels = new ArrayList<>();
        for (String channel : properties.getProperty("CHANNELS").split(",")) {
            channels.add(new Channel(channel));
        }

        // Behaviour
        bot.addPlugin(new AutoJoinPlugin(channels));
        bot.addPlugin(new RejoinOnKickPlugin());
        bot.addPlugin(new NickServPlugin(properties.getProperty("NICKSERV_PASSWORD")));

        // Commands
        bot.addPlugin(new UptimePlugin());
        bot.addPlugin(new OperatorPlugin());
        bot.addPlugin(new TranslatorPlugin());

        // Help
        bot.addPlugin(new HelpPlugin());
    }
}
