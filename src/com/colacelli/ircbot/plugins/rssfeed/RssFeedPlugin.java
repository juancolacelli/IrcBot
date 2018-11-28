package com.colacelli.ircbot.plugins.rssfeed;

import com.colacelli.ircbot.IRCBot;
import com.colacelli.ircbot.Plugin;
import com.colacelli.ircbot.listeners.OnChannelCommandListener;
import com.colacelli.ircbot.plugins.access.IRCBotAccess;
import com.colacelli.ircbot.plugins.help.PluginHelp;
import com.colacelli.ircbot.plugins.help.PluginHelper;
import com.colacelli.irclib.connection.Connection;
import com.colacelli.irclib.connection.listeners.OnPingListener;
import com.colacelli.irclib.messages.ChannelMessage;
import com.colacelli.irclib.messages.ChannelNoticeMessage;
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

public class RssFeedPlugin implements Plugin {
    private static final String PROPERTIES_URLS = "rss_feed_urls";
    private static final String PROPERTIES_URLS_SEPARATOR = ",";
    private static final String PROPERTIES_FILE = "rss_feed.properties";
    private ArrayList<RssFeed> rssFeeds;
    private Properties properties;
    private OnPingListener listener;

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

        listener = connection -> check(connection);
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
            urls.append(PROPERTIES_URLS_SEPARATOR);
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
    public String name() {
        return "RSS_FEED";
    }

    @Override
    public void onLoad(IRCBot bot) {
        // Check RSS feeds on server ping
        bot.addListener(listener);

        IRCBotAccess.getInstance().addListener(bot, IRCBotAccess.ADMIN_LEVEL, new OnChannelCommandListener() {
            @Override
            public String channelCommand() {
                return ".rss";
            }

            @Override
            public void onChannelCommand(Connection connection, ChannelMessage message, String command, String... args) {
                RssFeed rssFeed;
                PrivateNoticeMessage.Builder builder = new PrivateNoticeMessage.Builder();
                builder
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

                                    builder.setText(rssFeed.getUrl() + " added!");
                                } catch (IOException e) {
                                    builder.setText("Wrong RSS feed URL!");
                                }
                                connection.send(builder.build());

                                break;

                            case "del":
                                try {
                                    int feedIndex = Integer.parseInt(args[1]);
                                    rssFeed = rssFeeds.remove(feedIndex);
                                    saveProperties();

                                    builder.setText(rssFeed.getUrl() + " deleted!");
                                } catch (IndexOutOfBoundsException | NumberFormatException e) {
                                    builder.setText("Wrong RSS feed index!");
                                }

                                connection.send(builder.build());

                                break;
                        }
                    } else {
                        switch (args[0]) {
                            case "list":
                                for (int i = 0; i < rssFeeds.size(); i++) {
                                    rssFeed = rssFeeds.get(i);

                                    builder.setText(i + ": " + rssFeed.getUrl());
                                    connection.send(builder.build());
                                }

                                break;

                            case "check":
                                check(connection);

                                break;
                        }
                    }
                }
            }
        });

        PluginHelper.getInstance().addHelp(new PluginHelp(
                ".rss check",
                IRCBotAccess.ADMIN_LEVEL,
                "Check all RSS feeds"));
        PluginHelper.getInstance().addHelp(new PluginHelp(
                ".rss list",
                IRCBotAccess.ADMIN_LEVEL,
                "List all RSS feeds"));
        PluginHelper.getInstance().addHelp(new PluginHelp(
                ".rss add",
                IRCBotAccess.ADMIN_LEVEL,
                "Add a new RSS feed",
                "url"));
        PluginHelper.getInstance().addHelp(new PluginHelp(
                ".rss del",
                IRCBotAccess.ADMIN_LEVEL,
                "Delete an RSS feed",
                "index"));
    }

    @Override
    public void onUnload(IRCBot bot) {
        bot.removeListener(listener);
        IRCBotAccess.getInstance().removeListener(bot, ".rss");
        PluginHelper.getInstance().removeHelp(".rss check");
        PluginHelper.getInstance().removeHelp(".rss list");
        PluginHelper.getInstance().removeHelp(".rss add");
        PluginHelper.getInstance().removeHelp(".rss del");
    }

    private void check(Connection connection) {
        for (RssFeed rssFeed : rssFeeds) {
            RssChecker task = new RssChecker(rssFeed);

            // If it's the first time, don't publish items to prevent flood
            if (!rssFeed.justAdded()) {
                task.addListener(new OnRssFeedCheckListener() {
                    @Override
                    public void onSuccess(RssFeed rssFeed, ArrayList<RssFeedItem> rssFeedItems) {
                        if (!rssFeedItems.isEmpty()) {
                            RssFeedItem rssFeedItem = rssFeedItems.get(0);

                            connection.getChannels().forEach((channel) -> {
                                ChannelNoticeMessage.Builder builder = new ChannelNoticeMessage.Builder();
                                builder
                                        .setSender(connection.getUser())
                                        .setChannel(channel)
                                        .setText(rssFeedItem.toString());

                                connection.send(builder.build());
                            });
                        }
                    }

                    @Override
                    public void onError(RssFeed rssFeed) {
                        rssFeeds.remove(rssFeed);
                        saveProperties();
                    }
                });
            }

            Thread worker = new Thread(task);
            worker.setName("RssChecker");
            worker.start();
        }
    }

    private class RssChecker implements Runnable {
        private RssFeed rssFeed;
        private ArrayList<OnRssFeedCheckListener> onRssFeedCheckListeners;

        public RssChecker(RssFeed rssFeed) {
            this.rssFeed = rssFeed;
            this.onRssFeedCheckListeners = new ArrayList<>();
        }

        public void addListener(OnRssFeedCheckListener listener) {
            this.onRssFeedCheckListeners.add(listener);
        }

        @Override
        public void run() {
            ArrayList<RssFeedItem> rssFeedItems;
            try {
                rssFeedItems = rssFeed.check();
                if (!rssFeedItems.isEmpty()) {
                    for (OnRssFeedCheckListener listener : onRssFeedCheckListeners) {
                        listener.onSuccess(rssFeed, rssFeedItems);
                    }
                }
            } catch (IOException | ParserConfigurationException | SAXException e) {
                onRssFeedCheckListeners.forEach(listener -> listener.onError(rssFeed));
            }
        }
    }
}
