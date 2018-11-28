package com.colacelli.ircbot.plugins.access;

import com.colacelli.ircbot.IRCBot;
import com.colacelli.ircbot.Plugin;
import com.colacelli.ircbot.listeners.OnChannelCommandListener;
import com.colacelli.ircbot.plugins.help.PluginHelp;
import com.colacelli.ircbot.plugins.help.PluginHelper;
import com.colacelli.irclib.connection.Connection;
import com.colacelli.irclib.messages.ChannelMessage;
import com.colacelli.irclib.messages.PrivateNoticeMessage;

public class AccessPlugin implements Plugin {
    @Override
    public String getName() {
        return "ACCESS";
    }

    @Override
    public void onLoad(IRCBot bot) {
        IRCBotAccess.getInstance().addListener(bot, IRCBotAccess.SUPER_ADMIN_LEVEL, new OnChannelCommandListener() {
            @Override
            public String channelCommand() {
                return ".access";
            }

            @Override
            public void onChannelCommand(Connection connection, ChannelMessage message, String command, String... args) {
                if (args != null) {
                    PrivateNoticeMessage.Builder builder = new PrivateNoticeMessage.Builder();
                    builder
                            .setSender(connection.getUser())
                            .setReceiver(message.getSender());

                    if (args.length > 1) {
                        switch (args[0]) {
                            case "add":
                                try {
                                    int level = Integer.parseInt(args[2]);
                                    IRCBotAccess.getInstance().setLevel(args[1], level);
                                    builder.setText("Access granted to " + args[1]);
                                } catch (NumberFormatException e) {
                                    builder.setText("Invalid level");
                                    // Invalid level
                                }
                                connection.send(builder.build());
                                break;

                            case "del":
                                IRCBotAccess.getInstance().setLevel(args[1], 0);
                                builder.setText("Access revoked to " + args[1]);
                                connection.send(builder.build());
                                break;
                        }
                    } else {
                        switch (args[0]) {
                            case "list":
                                IRCBotAccess.getInstance().getAccesses().forEach((nick, level) -> {
                                    builder.setText(nick + ": " + level);
                                    connection.send(builder.build());
                                });
                                break;
                        }
                    }
                }
            }
        });

        PluginHelper.getInstance().addHelp(new PluginHelp(
                ".access add",
                IRCBotAccess.SUPER_ADMIN_LEVEL,
                "Grant user access",
                "user",
                "level"));

        PluginHelper.getInstance().addHelp(new PluginHelp(
                ".access del",
                IRCBotAccess.SUPER_ADMIN_LEVEL,
                "Revoke user access",
                "user"));

        PluginHelper.getInstance().addHelp(new PluginHelp(
                ".access list",
                IRCBotAccess.SUPER_ADMIN_LEVEL,
                "Show access list"));
    }

    @Override
    public void onUnload(IRCBot bot) {
        IRCBotAccess.getInstance().removeListener(bot, ".access");
        PluginHelper.getInstance().removeHelp(".access add");
        PluginHelper.getInstance().removeHelp(".access del");
        PluginHelper.getInstance().removeHelp(".access list");
    }
}