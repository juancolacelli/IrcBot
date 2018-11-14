package com.colacelli.ircbot.plugins.rssfeed;

import com.colacelli.ircbot.IRCBot;
import com.colacelli.ircbot.plugins.help.PluginWithHelp;
import com.colacelli.irclib.connection.Connection;
import com.colacelli.irclib.messages.ChannelMessage;
import com.colacelli.irclib.messages.PrivateNoticeMessage;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;

public class RssFeedPlugin implements PluginWithHelp {
    public static final String PROPERTIES_URLS = "rss_feed_urls";
    public static final String PROPERTIES_URLS_SEPARATOR = ",";
    private static final String PROPERTIES_FILE = "rss_feed.properties";
    private ArrayList<RssFeed> rssFeeds;
    private Properties properties;

    public RssFeedPlugin() {
        rssFeeds = new ArrayList<>();

        properties = loadProperties();
        String urls = properties.getProperty(PROPERTIES_URLS);

        if (urls != null && !urls.isEmpty()) {
            for (String url : urls.split(PROPERTIES_URLS_SEPARATOR)) {
                RssFeed rssFeed = new RssFeed(url);
                rssFeeds.add(rssFeed);
            }
        }
    }

    private Properties loadProperties() {
        properties = new Properties();

        try {
            FileInputStream fileInputStream = new FileInputStream(PROPERTIES_FILE);
            properties.load(fileInputStream);
        } catch (IOException e) {
            // Properties file not found
            properties = new Properties();
            saveProperties();
        }

        return properties;
    }

    private void saveProperties() {
        OutputStream outputStream = null;

        StringBuilder urls = new StringBuilder();
        rssFeeds.forEach(rssFeed -> {
            urls.append(rssFeed.getUrl());
            urls.append(",");
        });
        properties.setProperty(PROPERTIES_URLS, urls.toString());

        try {
            outputStream = new FileOutputStream(PROPERTIES_FILE);
            properties.store(outputStream, null);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void setup(IRCBot bot) {
        // Check RSS feeds on server ping
        bot.addListener((connection) -> check(connection));

        bot.addListener("!rss", (connection, message, command, args) -> {
            RssFeed rssFeed;
            PrivateNoticeMessage.Builder privateNoticeMessageBuilder = new PrivateNoticeMessage.Builder();
            privateNoticeMessageBuilder
                    .setSender(connection.getUser())
                    .setReceiver(message.getSender());

            if (args != null) {
                if (args.length > 1) {
                    switch (args[0]) {
                        case "add":
                            rssFeed = new RssFeed(args[1]);

                            try {
                                new URL(rssFeed.getUrl());
                                rssFeeds.add(rssFeed);
                                saveProperties();

                                privateNoticeMessageBuilder.setText(rssFeed.getUrl() + " added!");
                            } catch (IOException e) {
                                privateNoticeMessageBuilder.setText("Wrong RSS feed URL!");
                            }
                            connection.send(privateNoticeMessageBuilder.build());

                            break;

                        case "del":
                            try {
                                int feedIndex = Integer.parseInt(args[1]);
                                rssFeed = rssFeeds.remove(feedIndex);
                                saveProperties();

                                privateNoticeMessageBuilder.setText(rssFeed.getUrl() + " deleted!");
                            } catch (IndexOutOfBoundsException | NumberFormatException e) {
                                privateNoticeMessageBuilder.setText("Wrong RSS feed index!");
                            }

                            connection.send(privateNoticeMessageBuilder.build());

                            break;
                    }
                } else {
                    switch (args[0]) {
                        case "list":
                            for (int i = 0; i < rssFeeds.size(); i++) {
                                rssFeed = rssFeeds.get(i);

                                privateNoticeMessageBuilder.setText(i + ": " + rssFeed.getUrl());
                                connection.send(privateNoticeMessageBuilder.build());
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
        for (RssFeed rssFeed : rssFeeds) {
            Runnable task = new RssChecker(connection, rssFeed);

            ((RssChecker) task).addListener(new OnRssFeedCheckListener() {
                @Override
                public void onSuccess(RssFeed rssFeed, ArrayList<RssFeedItem> rssFeedItems) {
                    if (!rssFeedItems.isEmpty()) {
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
                }

                @Override
                public void onError(RssFeed rssFeed) {
                    rssFeeds.remove(rssFeed);
                    saveProperties();
                }
            });

            Thread worker = new Thread(task);
            worker.setName("RssChecker");
            worker.start();
        }
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
        private RssFeed rssFeed;
        private ArrayList<OnRssFeedCheckListener> onRssFeedCheckListeners;

        public RssChecker(Connection connection, RssFeed rssFeed) {
            this.connection = connection;
            this.rssFeed = rssFeed;
            this.onRssFeedCheckListeners = new ArrayList<>();
        }

        public void addListener(OnRssFeedCheckListener listener) {
            this.onRssFeedCheckListeners.add(listener);
        }

        @Override
        public void run() {
            ArrayList<RssFeedItem> rssFeedItems = null;
            try {
                rssFeedItems = rssFeed.check();
                if (!rssFeedItems.isEmpty()) {
                    // Use just the first item
                    RssFeedItem rssFeedItem = rssFeedItems.get(0);

                    for (int i = 0; i < onRssFeedCheckListeners.size(); i++) {
                        onRssFeedCheckListeners.get(i).onSuccess(rssFeed, rssFeedItems);
                    }
                }
            } catch (IOException | ParserConfigurationException | SAXException e) {
                onRssFeedCheckListeners.forEach(listener -> listener.onError(rssFeed));
            }
        }
    }
}
