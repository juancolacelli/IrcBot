package com.colacelli.samplebot.plugins.operator;

import com.colacelli.ircbot.IRCBot;
import com.colacelli.ircbot.plugins.Plugin;
import com.colacelli.irclib.actors.Channel;
import com.colacelli.irclib.actors.User;
import com.colacelli.samplebot.plugins.help.PluginWithHelp;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class OperatorPlugin implements PluginWithHelp {
    @Override
    public void setup(IRCBot bot) {
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

        for (Map.Entry<String, String> entry : commandModes.entrySet()) {
            String text = entry.getKey();
            String mode = entry.getValue();

            bot.addListener(text, (connection, message, command, args) -> {
                String nicks[] = {message.getSender().getNick()};
                if (args != null) nicks = args;

                StringBuilder repeatedMode = new StringBuilder();
                for (String ignored : nicks) {
                    repeatedMode.append(mode);
                }

                connection.mode(message.getChannel(), repeatedMode + " " + String.join(" ", nicks));
            });
        }

        bot.addListener("!join", (connection, message, command, args) -> {
            if (args != null) {
                connection.join(new Channel(args[0]));
            }
        });

        bot.addListener("!part", (connection, message, command, args) -> {
            Channel channel = args == null ? message.getChannel() : new Channel(args[0]);
            connection.part(channel);
        });

        bot.addListener("!mode", (connection, message, command, args) -> {
            if (args != null) {
                String modes = String.join(" ", args);
                connection.mode(message.getChannel(), modes);
            }
        });

        bot.addListener("!kick", (connection, message, command, args) -> {
            if (args != null) {
                String nick = args[0];
                String reason = args.length > 1 ? String.join(" ", Arrays.copyOfRange(args, 1, args.length)) : "...";
                connection.kick(message.getChannel(), new User(nick), reason);
            }
        });

        bot.addListener("!topic", (connection, message, command, args) -> {
            if (args != null) {
                String topic = String.join(" ", args);
                connection.topic(message.getChannel(), topic);
            }
        });
    }

    @Override
    public String[] getHelp() {
        return new String[]{
                "!owner/!deowner ?<nick>: Gives/Removes +q mode",
                "!protect/!deprotect ?<nick>: Gives/Removes +a mode",
                "!op/!deop ?<nick>: Gives/Removes +o mode",
                "!halfop/!dehalfop ?<nick>: Gives/Removes +h mode",
                "!voice/!devoice ?<nick>: Gives/Removes +v mode",
                "!join <channel>: Joins a channel",
                "!part ?<channel>: Parts from channel",
                "!mode <modes>: Sets modes on current channel",
                "!kick <user> ?<reason>: Kicks a user from current channel",
                "!topic <topic>: Changes current channel topic"
        };
    }
}
