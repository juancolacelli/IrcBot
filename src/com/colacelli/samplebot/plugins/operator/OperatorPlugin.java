package com.colacelli.samplebot.plugins.operator;

import com.colacelli.ircbot.IRCBot;
import com.colacelli.ircbot.plugins.Plugin;
import com.colacelli.irclib.actors.Channel;
import com.colacelli.irclib.connection.listeners.OnJoinListener;
import com.colacelli.irclib.messages.ChannelMessage;

import java.util.HashMap;
import java.util.Map;

public class OperatorPlugin implements Plugin {
    @Override
    public void setup(IRCBot bot) {
        bot.addListener((OnJoinListener) (connection, user, channel) -> {
            if (user.getNick() != connection.getUser().getNick()) {
                ChannelMessage.Builder channelMessageBuilder = new ChannelMessage.Builder();
                channelMessageBuilder
                        .setSender(connection.getUser())
                        .setChannel(channel)
                        .setText("Hello " + user.getNick() + " welcome to " + channel.getName());

                if (!user.getNick().equals(connection.getUser().getNick())) {
                    connection.send(channelMessageBuilder.build());
                }
            }
        });

        HashMap<String, String> commandModes = new HashMap<>();
        commandModes.put("!owner", "+q");
        commandModes.put("!deowner", "-q");
        commandModes.put("!protect", "+a");
        commandModes.put("!deprotect", "-a");
        commandModes.put("!op", "+o");
        commandModes.put("!deop", "-o");
        commandModes.put("!halfop", "+h");
        commandModes.put("!dehalfop", "-h");
        commandModes.put("!voice", "+v");
        commandModes.put("!devoice", "-v");

        for(Map.Entry<String, String> entry : commandModes.entrySet()) {
            String text = entry.getKey();
            String mode = entry.getValue();

            bot.addListener(text, (connection, message, command, args) -> {
                String nick = message.getSender().getNick();

                if (args != null) nick = args[0];

                // Bot can't change it's own modes
                if (!nick.equals(connection.getUser().getNick())) {
                    connection.mode(message.getChannel(), mode + " " + nick);
                }
            });
        }

        bot.addListener("!join", (connection, message, command, args) -> {
            if (args != null) {
                String channel = args[0];

                connection.join(new Channel(channel));
            }
        });

        bot.addListener("!part", (connection, message, command, args) -> {
            Channel channel = message.getChannel();
            if (args != null) channel = new Channel(args[0]);

            connection.part(channel);
        });

        bot.addListener("!mode", (connection, message, command, args) -> {
            String modes = String.join(" ", args);

            if (modes != null) {
                connection.mode(message.getChannel(), modes);
            }
        });
    }
}
