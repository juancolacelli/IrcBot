package com.colacelli.ircbot.plugins.operator;

import com.colacelli.ircbot.IRCBot;
import com.colacelli.ircbot.Plugin;
import com.colacelli.ircbot.plugins.access.IRCBotAccess;
import com.colacelli.ircbot.plugins.help.PluginHelp;
import com.colacelli.ircbot.plugins.help.PluginHelper;
import com.colacelli.irclib.actors.Channel;
import com.colacelli.irclib.actors.User;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class OperatorPlugin implements Plugin {
    @Override
    public void setup(IRCBot bot) {
        HashMap<String, String> commandModes = new HashMap<>();
        commandModes.put(".owner", "+q");
        commandModes.put(".deowner", "-q");
        commandModes.put(".protect", "+a");
        commandModes.put(".deprotect", "-a");
        commandModes.put(".op", "+o");
        commandModes.put(".deop", "-o");
        commandModes.put(".halfop", "+h");
        commandModes.put(".dehalfop", "-h");
        commandModes.put(".voice", "+v");
        commandModes.put(".devoice", "-v");

        for (Map.Entry<String, String> entry : commandModes.entrySet()) {
            String text = entry.getKey();
            String mode = entry.getValue();

            IRCBotAccess.getInstance().addListener(bot, text, IRCBotAccess.OPERATOR_LEVEL, (connection, message, command, args) -> {
                String nicks[] = {message.getSender().getNick()};
                if (args != null) nicks = args;

                StringBuilder repeatedMode = new StringBuilder();
                for (String ignored : nicks) {
                    repeatedMode.append(mode);
                }

                connection.mode(message.getChannel(), repeatedMode + " " + String.join(" ", nicks));
            });

            PluginHelper.getInstance().addHelp(new PluginHelp(
                    text,
                    IRCBotAccess.OPERATOR_LEVEL,
                    mode + " user channel mode",
                    "#channel",
                    "user"));
        }

        IRCBotAccess.getInstance().addListener(bot, ".join", IRCBotAccess.OPERATOR_LEVEL,
                (connection, message, command, args) -> {
                    if (args != null) {
                        connection.join(new Channel(args[0]));
                    }
                });
        PluginHelper.getInstance().addHelp(new PluginHelp(
                ".join",
                IRCBotAccess.OPERATOR_LEVEL,
                "Joins a channel",
                "#channel"));

        IRCBotAccess.getInstance().addListener(bot, ".part", IRCBotAccess.OPERATOR_LEVEL, (connection, message, command, args) -> {
            Channel channel = args == null ? message.getChannel() : new Channel(args[0]);
            connection.part(channel);
        });
        PluginHelper.getInstance().addHelp(new PluginHelp(
                ".part",
                IRCBotAccess.OPERATOR_LEVEL,
                "Parts from a channel",
                "#channel"));

        IRCBotAccess.getInstance().addListener(bot, ".mode", IRCBotAccess.OPERATOR_LEVEL, (connection, message, command, args) -> {
            if (args != null) {
                String modes = String.join(" ", args);
                connection.mode(message.getChannel(), modes);
            }
        });
        PluginHelper.getInstance().addHelp(new PluginHelp(
                ".mode",
                IRCBotAccess.OPERATOR_LEVEL,
                "Changes channel modes",
                "#channel",
                "modes"));

        IRCBotAccess.getInstance().addListener(bot, ".kick", IRCBotAccess.OPERATOR_LEVEL, (connection, message, command, args) -> {
            if (args != null) {
                String nick = args[0];
                String reason = args.length > 1 ? String.join(" ", Arrays.copyOfRange(args, 1, args.length)) : "...";
                connection.kick(message.getChannel(), new User(nick), reason);
            }
        });
        PluginHelper.getInstance().addHelp(new PluginHelp(
                ".kick",
                IRCBotAccess.OPERATOR_LEVEL,
                "Kicks a user from channel",
                "#channel",
                "nick",
                "reason"));

        IRCBotAccess.getInstance().addListener(bot, ".topic", IRCBotAccess.OPERATOR_LEVEL, (connection, message, command, args) -> {
            if (args != null) {
                String topic = String.join(" ", args);
                connection.topic(message.getChannel(), topic);
            }
        });
        PluginHelper.getInstance().addHelp(new PluginHelp(
                ".topic",
                IRCBotAccess.OPERATOR_LEVEL,
                "Changes channel topic",
                "#channel",
                "topic"));
    }
}
