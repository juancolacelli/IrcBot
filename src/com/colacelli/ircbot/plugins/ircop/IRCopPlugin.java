package com.colacelli.ircbot.plugins.ircop;

import com.colacelli.ircbot.IRCBot;
import com.colacelli.ircbot.Plugin;
import com.colacelli.ircbot.listeners.OnChannelCommandListener;
import com.colacelli.ircbot.plugins.access.IRCBotAccess;
import com.colacelli.ircbot.plugins.help.PluginHelp;
import com.colacelli.ircbot.plugins.help.PluginHelper;
import com.colacelli.irclib.actors.User;
import com.colacelli.irclib.connection.Connection;
import com.colacelli.irclib.connection.listeners.OnConnectListener;
import com.colacelli.irclib.messages.ChannelMessage;

import java.util.Arrays;

public class IRCopPlugin implements Plugin {
    private String name;
    private String password;
    private OnConnectListener listener;

    public IRCopPlugin(String name, String password) {
        this.name = name;
        this.password = password;
    }

    @Override
    public String getName() {
        return "IRCOP";
    }

    @Override
    public void onLoad(IRCBot bot) {
        // TODO: Add more IRCop commands
        listener = (connection, server, user) -> connection.send("OPER " + name + " " + password);
        bot.addListener(listener);

        IRCBotAccess.getInstance().addListener(bot, IRCBotAccess.ADMIN_LEVEL, new OnChannelCommandListener() {
            @Override
            public String channelCommand() {
                return ".kill";
            }

            @Override
            public void onChannelCommand(Connection connection, ChannelMessage message, String command, String... args) {
                if (args != null) {
                    String nick = args[0];

                    if (!connection.getUser().getNick().toLowerCase().equals(nick.toLowerCase())) {
                        String reason = args.length > 1 ? String.join(" ", Arrays.copyOfRange(args, 1, args.length)) : "...";
                        connection.kill(new User(nick), reason);
                    }
                }
            }
        });
        PluginHelper.getInstance().addHelp(new PluginHelp(
                ".kill",
                IRCBotAccess.OPERATOR_LEVEL,
                "Kills a user from server",
                "nick",
                "reason"));
    }

    @Override
    public void onUnload(IRCBot bot) {
        bot.removeListener(listener);
        bot.removeListener(".kill");
    }
}
