package com.colacelli.ircbot.plugins.websitetitle;

import com.colacelli.ircbot.IRCBot;
import com.colacelli.ircbot.Plugin;
import com.colacelli.irclib.connection.listeners.OnChannelMessageListener;
import com.colacelli.irclib.messages.ChannelMessage;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebsiteTitlePlugin implements Plugin {
    @Override
    public void setup(IRCBot bot) {
        bot.addListener((OnChannelMessageListener) (connection, message) -> {
            String text = message.getText();
            Pattern urlsPattern = Pattern.compile("((http://|https://)([^ ]+))");
            Matcher urlsMatcher = urlsPattern.matcher(text);
            while (urlsMatcher.find()) {
                try {
                    try {
                        String url = urlsMatcher.group(0);

                        // Body
                        InputStream response = new URL(url).openStream();

                        Scanner scanner = new Scanner(response).useDelimiter("\\A");
                        String title = "";

                        while (title.isEmpty() && scanner.hasNext()) {
                            String responseBody = scanner.next();

                            // Title
                            Pattern titlePattern = Pattern.compile("<title>(.+?)</title>");
                            Matcher titleMatch = titlePattern.matcher(responseBody);
                            titleMatch.find();

                            try {
                                title = titleMatch.group(1);

                                ChannelMessage.Builder channelMessageBuilder = new ChannelMessage.Builder();
                                channelMessageBuilder
                                        .setChannel(message.getChannel())
                                        .setSender(connection.getUser())
                                        .setText(title + " - " + url);

                                connection.send(channelMessageBuilder.build());
                            } catch (IllegalStateException | NoSuchElementException e) {
                                // Title not found
                            }
                        }
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
