package com.colacelli.samplebot.plugins.uptime;

import com.colacelli.ircbot.IRCBot;
import com.colacelli.ircbot.plugins.Plugin;
import com.colacelli.irclib.messages.ChannelMessage;

import java.util.Date;

public class UptimePlugin implements Plugin {
    private final Date startDate;

    public UptimePlugin() {
        startDate = new Date(System.currentTimeMillis());
    }

    @Override
    public void setup(IRCBot bot) {
        bot.addListener("!uptime", (connection, message, command, args) -> {
            long currentTimeMillis = System.currentTimeMillis();
            long startMillis = startDate.getTime();

            long diff = currentTimeMillis - startMillis;
            long seconds = diff / 1000 % 60;
            long minutes = diff / (60 * 1000) % 60;
            long hours = diff / (60 * 60 * 1000) % 24;
            long days = diff / (60 * 60 * 1000 * 24);

            String uptime = String.format("%dd %02d:%02d:%02d", days, hours, minutes, seconds);

            ChannelMessage.Builder channelMessageBuilder = new ChannelMessage.Builder();
            channelMessageBuilder
                    .setSender(connection.getUser())
                    .setChannel(message.getChannel())
                    .setText("Uptime: " + uptime);

            connection.send(channelMessageBuilder.build());
        });
    }
}
