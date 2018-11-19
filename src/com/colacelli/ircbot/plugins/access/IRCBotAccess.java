package com.colacelli.ircbot.plugins.access;

import com.colacelli.ircbot.IRCBot;
import com.colacelli.ircbot.listeners.OnChannelCommandListener;
import com.colacelli.irclib.actors.User;
import com.colacelli.irclib.connection.Connection;
import com.colacelli.irclib.connection.Rawable;
import com.colacelli.irclib.connection.listeners.OnRawCodeListener;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Properties;

public class IRCBotAccess {
    public static final int ADMIN_LEVEL = 10;
    public static final int OPERATOR_LEVEL = 5;

    private static final String PROPERTIES_FILE = "access.properties";

    private static IRCBotAccess instance;
    private Properties properties;


    private IRCBotAccess() {
        properties = new Properties();
    }

    public static IRCBotAccess getInstance() {
        if (instance == null) {
            instance = new IRCBotAccess();
        }

        return instance;
    }

    public void addListener(IRCBot bot, String command, int access, OnChannelCommandListener listener) {
        bot.addListener(command, (connection, message, command1, args) -> {
            if (IRCBotAccess.getInstance().getLevel(message.getSender()) >= access) {
                // Check by whois if nick is identified
                OnRawCodeListener rawListener = (connection1, message1, rawCode, args1) -> {
                    // Same nickname?
                    if (args1[3].equals(message.getSender().getNick())) {
                        listener.onChannelCommand(connection, message, command1, args);
                    }
                };
                bot.addListener(Rawable.RawCode.WHOIS_IDENTIFIED_NICK.getCode(), rawListener);

                // Whois end?
                bot.addListener(Rawable.RawCode.WHOIS_END.getCode(), new OnRawCodeListener() {
                    @Override
                    public void onRawCode(Connection connection1, String message1, int rawCode, String... args1) {
                        // Same nickname?
                        if (args1[3].equals(message.getSender().getNick())) {
                            // Remove whois listeners
                            bot.removeListener(Rawable.RawCode.WHOIS_IDENTIFIED_NICK.getCode(), rawListener);
                            bot.removeListener(rawCode, this);
                        }
                    }
                });

                connection.whois(message.getSender());
            }
        });
    }

    public void setLevel(String nick, int level) {
        loadProperties();

        nick = nick.toUpperCase();

        if (level > 0) {
            properties.setProperty(nick, String.valueOf(level));
        } else {
            properties.setProperty(nick, "");
        }

        saveProperties();
    }

    public int getLevel(User user) {
        return getLevel(user.getNick());
    }

    public int getLevel(String nick) {
        loadProperties();

        int level;
        try {
            level = Integer.parseInt(properties.getProperty(nick.toUpperCase()));
        } catch (NumberFormatException e) {
            level = 0;
        }

        return level;
    }

    public HashMap<String, Integer> getAccesses() {
        loadProperties();

        HashMap<String, Integer> accesses = new HashMap<>();
        properties.forEach((key, value) -> {
            try {
                accesses.put(String.valueOf(key), Integer.valueOf(String.valueOf(value)));
            } catch (NumberFormatException e) {
                // Invalid level
            }
        });

        return accesses;
    }

    private void loadProperties() {
        try {
            FileInputStream fileInputStream = new FileInputStream(PROPERTIES_FILE);
            properties.load(fileInputStream);
        } catch (IOException e) {
            // Properties file not found
            properties = new Properties();
            saveProperties();
        }
    }

    private void saveProperties() {
        OutputStream outputStream = null;

        try {
            outputStream = new FileOutputStream(PROPERTIES_FILE);
            properties.store(outputStream, null);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
