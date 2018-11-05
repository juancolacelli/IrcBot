package com.colacelli.samplebot.plugins.websitetitle;

import com.colacelli.ircbot.IRCBot;
import com.colacelli.ircbot.plugins.Plugin;
import com.colacelli.irclib.connection.listeners.OnChannelMessageListener;
import com.colacelli.irclib.messages.ChannelMessage;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebsiteTitlePlugin implements Plugin {
    @Override
    public void setup(IRCBot bot) {
        bot.addListener((OnChannelMessageListener) (connection, message) -> {
            String text = message.getText();
            if (text.startsWith("http://") || text.startsWith("https://")) {
                try {
                    try {
                        // Body
                        InputStream response = new URL(text).openStream();

                        Scanner scanner = new Scanner(response);
                        String responseBody = scanner.useDelimiter("\\A").next();

                        // Title
                        Pattern titlePattern = Pattern.compile("<title>(.+?)</title>");
                        Matcher titleMatch = titlePattern.matcher(responseBody);
                        titleMatch.find();

                        String title = titleMatch.group(1);

                        ChannelMessage.Builder channelMessageBuilder = new ChannelMessage.Builder();
                        channelMessageBuilder
                                .setChannel(message.getChannel())
                                .setSender(connection.getUser())
                                .setText("Title: " + title);

                        connection.send(channelMessageBuilder.build());
                    } catch (IllegalArgumentException e) {
                        // Invalid URL
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
