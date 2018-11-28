package com.colacelli.ircbot.plugins.joinpart;

import com.colacelli.ircbot.IRCBot;
import com.colacelli.ircbot.Plugin;
import com.colacelli.ircbot.listeners.OnChannelCommandListener;
import com.colacelli.ircbot.plugins.access.IRCBotAccess;
import com.colacelli.ircbot.plugins.help.PluginHelp;
import com.colacelli.ircbot.plugins.help.PluginHelper;
import com.colacelli.irclib.actors.Channel;
import com.colacelli.irclib.connection.Connection;
import com.colacelli.irclib.messages.ChannelMessage;

public class JoinPartPlugin implements Plugin {
    @Override
    public String getName() {
        return "JOIN_PART";
    }

    @Override
    public void onLoad(IRCBot bot) {
        IRCBotAccess.getInstance().addListener(bot, IRCBotAccess.OPERATOR_LEVEL, new OnChannelCommandListener() {
            @Override
            public String channelCommand() {
                return ".join";
            }

            @Override
            public void onChannelCommand(Connection connection, ChannelMessage message, String command, String... args) {
                if (args != null) {
                    connection.join(new Channel(args[0]));
                }
            }
        });
        PluginHelper.getInstance().addHelp(new PluginHelp(
                ".join",
                IRCBotAccess.OPERATOR_LEVEL,
                "Joins a channel",
                "#channel"));

        IRCBotAccess.getInstance().addListener(bot, IRCBotAccess.OPERATOR_LEVEL, new OnChannelCommandListener() {
            @Override
            public String channelCommand() {
                return ".part";
            }

            @Override
            public void onChannelCommand(Connection connection, ChannelMessage message, String command, String... args) {
                Channel channel = args == null ? message.getChannel() : new Channel(args[0]);
                connection.part(channel);
            }
        });
        PluginHelper.getInstance().addHelp(new PluginHelp(
                ".part",
                IRCBotAccess.OPERATOR_LEVEL,
                "Parts from a channel",
                "#channel"));
    }

    @Override
    public void onUnload(IRCBot bot) {
        String[] commands = {".join", ".part"};
        for (String command : commands) {
            PluginHelper.getInstance().removeHelp(command);
            IRCBotAccess.getInstance().removeListener(bot, command);
        }
    }
}
