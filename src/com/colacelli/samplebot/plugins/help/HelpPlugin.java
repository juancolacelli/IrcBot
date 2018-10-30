package com.colacelli.samplebot.plugins.help;

import com.colacelli.ircbot.IRCBot;
import com.colacelli.ircbot.plugins.Plugin;
import com.colacelli.irclib.connection.Connection;
import com.colacelli.irclib.messages.ChannelMessage;
import com.colacelli.irclib.messages.PrivateMessage;

import java.util.ArrayList;

public class HelpPlugin implements Plugin {
    private ArrayList<String> helps;

    public HelpPlugin() {
        helps = new ArrayList<>();
    }

    @Override
    public void setup(IRCBot bot) {
        for (Plugin plugin : bot.getPlugins()) {
            try {
                String[] pluginHelps = ((PluginWithHelp) plugin).getHelp();

                for (String help : pluginHelps) {
                    helps.add(help);
                }
            } catch (ClassCastException e) {
                // Plugins can choose to extend Plugin or PluginWithHelp
            }
        }

        bot.addListener("!help", (connection, message, command, args) -> showHelp(connection, message, command, args));
    }

    private void showHelp(Connection connection, ChannelMessage message, String command, String... args) {
        for (String help : helps) {
            PrivateMessage.Builder privateMessageBuilder = new PrivateMessage.Builder();
            privateMessageBuilder
                    .setSender(connection.getUser())
                    .setReceiver(message.getSender())
                    .setText(help);

            connection.send(privateMessageBuilder.build());
        }
    }
}
