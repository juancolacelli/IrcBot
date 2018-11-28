package com.colacelli.ircbot.plugins.loader;

import com.colacelli.ircbot.IRCBot;
import com.colacelli.ircbot.Plugin;
import com.colacelli.ircbot.listeners.OnChannelCommandListener;
import com.colacelli.ircbot.plugins.access.IRCBotAccess;
import com.colacelli.ircbot.plugins.help.PluginHelp;
import com.colacelli.ircbot.plugins.help.PluginHelper;
import com.colacelli.irclib.connection.Connection;
import com.colacelli.irclib.messages.ChannelMessage;
import com.colacelli.irclib.messages.PrivateNoticeMessage;

import java.util.ArrayList;

public class LoaderPlugin implements Plugin {
    @Override
    public String getName() {
        return "LOADER";
    }

    @Override
    public void onLoad(IRCBot bot) {
        IRCBotAccess.getInstance().addListener(bot, IRCBotAccess.SUPER_ADMIN_LEVEL, new OnChannelCommandListener() {
            @Override
            public String channelCommand() {
                return ".plugin";
            }

            @Override
            public void onChannelCommand(Connection connection, ChannelMessage message, String command, String... args) {
                if (args != null) {
                    ArrayList<Plugin> botPlugins = bot.getPlugins();
                    PrivateNoticeMessage.Builder builder = new PrivateNoticeMessage.Builder();
                    builder
                            .setSender(connection.getUser())
                            .setReceiver(message.getSender());

                    if (args.length > 1) {
                        switch (args[0]) {
                            case "load":
                                for (int i = 0; i < botPlugins.size(); i++) {
                                    Plugin plugin = botPlugins.get(i);
                                    if (plugin.getName().toUpperCase().equals(args[1].toUpperCase())) {
                                        plugin.onLoad(bot);
                                        builder.setText(plugin.getName() + " loaded!");
                                        connection.send(builder.build());
                                    }
                                }
                                break;

                            case "unload":
                                for (int i = 0; i < botPlugins.size(); i++) {
                                    Plugin plugin = botPlugins.get(i);
                                    if (plugin.getName().toUpperCase().equals(args[1].toUpperCase())) {
                                        plugin.onUnload(bot);
                                        builder.setText(plugin.getName() + " unloaded!");
                                        connection.send(builder.build());
                                    }
                                }
                                break;
                        }
                    } else {
                        switch (args[0]) {
                            case "list":
                                botPlugins.forEach((plugin) -> {
                                    builder.setText(plugin.getName());
                                    connection.send(builder.build());
                                });
                                break;
                        }
                    }
                }
            }
        });

        PluginHelper.getInstance().addHelp(new PluginHelp(
                ".plugin list",
                IRCBotAccess.SUPER_ADMIN_LEVEL,
                "List all available plugins"));

        PluginHelper.getInstance().addHelp(new PluginHelp(
                ".plugin load",
                IRCBotAccess.SUPER_ADMIN_LEVEL,
                "Load a plugin",
                "plugin"));

        PluginHelper.getInstance().addHelp(new PluginHelp(
                ".plugin unload",
                IRCBotAccess.SUPER_ADMIN_LEVEL,
                "Unload a plugin",
                "plugin"));
    }

    @Override
    public void onUnload(IRCBot bot) {
        IRCBotAccess.getInstance().removeListener(bot, ".plugin");
        PluginHelper.getInstance().removeHelp(".plugin list");
        PluginHelper.getInstance().removeHelp(".plugin load");
        PluginHelper.getInstance().removeHelp(".plugin unload");
    }
}
