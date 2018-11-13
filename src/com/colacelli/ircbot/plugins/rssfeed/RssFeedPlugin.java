package com.colacelli.ircbot.plugins.rssfeed;

import com.colacelli.ircbot.IRCBot;
import com.colacelli.ircbot.Plugin;
import com.colacelli.ircbot.plugins.help.PluginWithHelp;
import com.colacelli.irclib.connection.Connection;
import com.colacelli.irclib.messages.ChannelMessage;
import com.colacelli.irclib.messages.PrivateMessage;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class RssFeedPlugin implements PluginWithHelp {
    ArrayList<RssFeed> rssFeeds;

    public RssFeedPlugin(String[] urls) {
        rssFeeds = new ArrayList<>();

        for (String url : urls) {
            RssFeed rssFeed = new RssFeed(url);
            rssFeeds.add(rssFeed);
        }
    }

    @Override
    public void setup(IRCBot bot) {
        // Check RSS feeds on server ping
        bot.addListener((connection) -> check(connection));

        bot.addListener("!rss", (connection, message, command, args) -> {
            RssFeed rssFeed;
            PrivateMessage.Builder privateMessageBuilder = new PrivateMessage.Builder();
            privateMessageBuilder
                    .setSender(connection.getUser())
                    .setReceiver(message.getSender());

            if (args != null) {
                if (args.length > 1) {
                    switch (args[0]) {
                        case "add":
                            rssFeed = new RssFeed(args[1]);

                            try {
                                URL url = new URL(rssFeed.getUrl());
                                rssFeeds.add(rssFeed);

                                privateMessageBuilder.setText(rssFeed.getUrl() + " added!");
                            } catch (IOException e) {
                                privateMessageBuilder.setText("Wrong RSS feed URL!");
                            }
                            connection.send(privateMessageBuilder.build());

                            break;

                        case "del":
                            try {
                                int feedIndex = Integer.parseInt(args[1]);
                                rssFeed = rssFeeds.remove(feedIndex);

                                privateMessageBuilder.setText(rssFeed.getUrl() + " deleted!");
                            } catch (IndexOutOfBoundsException | NumberFormatException e) {
                                privateMessageBuilder.setText("Wrong RSS feed index!");
                            }

                            connection.send(privateMessageBuilder.build());

                            break;
                    }
                } else {
                    switch (args[0]) {
                        case "list":
                            for (int i = 0; i < rssFeeds.size(); i++) {
                                rssFeed = rssFeeds.get(i);

                                privateMessageBuilder.setText(i + ": " + rssFeed.getUrl());
                                connection.send(privateMessageBuilder.build());
                            }

                            break;

                        case "check":
                            check(connection);

                            break;
                    }
                }
            }
        });
    }

    private void check(Connection connection) {
        Runnable task = new RssChecker(connection);
        Thread worker = new Thread(task);
        worker.setName("RssChecker");
        worker.start();
    }

    @Override
    public String[] getHelp() {
        return new String[]{
                "!rss check: Check all RSS feeds",
                "!rss list: List all RSS feeds",
                "!rss add <url>: Add a new RSS feed",
                "!rss del <index>: Deleete a RSS feed"
        };
    }

    private class RssChecker implements Runnable {
        private Connection connection;

        public RssChecker(Connection connection) {
            this.connection = connection;
        }

        @Override
        public void run() {
            for (RssFeed rssFeed : rssFeeds) {
                try {
                    ArrayList<RssFeedItem> rssFeedItems = rssFeed.check();

                    if (!rssFeedItems.isEmpty()) {
                        // Use just the first item
                        RssFeedItem rssFeedItem = rssFeedItems.get(0);

                        connection.getChannels().forEach((channel) -> {
                            ChannelMessage.Builder channelMessageBuilder = new ChannelMessage.Builder();
                            channelMessageBuilder
                                    .setSender(connection.getUser())
                                    .setChannel(channel)
                                    .setText(rssFeedItem.toString());

                            connection.send(channelMessageBuilder.build());
                        });
                    }
                } catch (SAXException | IOException | ParserConfigurationException e) {
                    // Not a RSS feed
                    rssFeeds.remove(rssFeed);
                }
            }
        }
    }
}
