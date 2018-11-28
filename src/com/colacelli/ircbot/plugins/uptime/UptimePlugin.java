package com.colacelli.ircbot.plugins.uptime;

import com.colacelli.ircbot.IRCBot;
import com.colacelli.ircbot.Plugin;
import com.colacelli.ircbot.listeners.OnChannelCommandListener;
import com.colacelli.ircbot.plugins.help.PluginHelp;
import com.colacelli.ircbot.plugins.help.PluginHelper;
import com.colacelli.irclib.connection.Connection;
import com.colacelli.irclib.messages.ChannelMessage;

import java.util.Date;

public class UptimePlugin implements Plugin {
    private final Date startDate;

    public UptimePlugin() {
        startDate = new Date(System.currentTimeMillis());
    }

    @Override
    public String name() {
        return "UPTIME";
    }

    @Override
    public void onLoad(IRCBot bot) {
        bot.addListener(new OnChannelCommandListener() {
            @Override
            public String channelCommand() {
                return ".uptime";
            }

            @Override
            public void onChannelCommand(Connection connection, ChannelMessage message, String command, String... args) {
                long currentTimeMillis = System.currentTimeMillis();
                long startMillis = startDate.getTime();

                long diff = currentTimeMillis - startMillis;
                long seconds = diff / 1000 % 60;
                long minutes = diff / (60 * 1000) % 60;
                long hours = diff / (60 * 60 * 1000) % 24;
                long days = diff / (60 * 60 * 1000 * 24);

                String uptime = String.format("%dd %02d:%02d:%02d", days, hours, minutes, seconds);

                ChannelMessage.Builder builder = new ChannelMessage.Builder();
                builder
                        .setSender(connection.getUser())
                        .setChannel(message.getChannel())
                        .setText("Uptime: " + uptime);

                connection.send(builder.build());
            }
        });
        PluginHelper.getInstance().addHelp(new PluginHelp(
                ".uptime",
                "Shows bot uptime"));
    }

    @Override
    public void onUnload(IRCBot bot) {
        bot.removeListener(".uptime");
        PluginHelper.getInstance().removeHelp(".uptime");
    }
}
