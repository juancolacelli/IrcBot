package com.colacelli.ircbot.plugins.rssfeed;

import com.colacelli.ircbot.IRCBot;
import com.colacelli.ircbot.Plugin;
import com.colacelli.ircbot.listeners.OnChannelCommandListener;
import com.colacelli.ircbot.plugins.access.IRCBotAccess;
import com.colacelli.ircbot.plugins.help.PluginHelp;
import com.colacelli.ircbot.plugins.help.PluginHelper;
import com.colacelli.irclib.actors.User;
import com.colacelli.irclib.connection.Connection;
import com.colacelli.irclib.connection.listeners.OnPingListener;
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

public class RssFeedPlugin implements Plugin {
    private static final String PROPERTIES_SEPARATOR = ",";
    private static final String PROPERTIES_URLS = "urls";
    private static final String PROPERTIES_SUBSCRIBERS = "subscribers";
    private static final String PROPERTIES_FILE = "rss_feed.properties";
    private ArrayList<RssFeed> rssFeeds;
    private ArrayList<String> subscribers;
    private Properties properties;
    private OnPingListener listener;

    public RssFeedPlugin() {
        rssFeeds = new ArrayList<>();
        subscribers = new ArrayList<>();

        properties = loadProperties();
        String urls_property = properties.getProperty(PROPERTIES_URLS);
        String subscribers_property = properties.getProperty(PROPERTIES_SUBSCRIBERS);

        if (urls_property != null && !urls_property.isEmpty()) {
            for (String url : urls_property.split(PROPERTIES_SEPARATOR)) {
                RssFeed rssFeed = new RssFeed(url);
                rssFeeds.add(rssFeed);
            }
        }

        if (subscribers_property != null && !subscribers_property.isEmpty()) {
            for (String subscriber : subscribers_property.split(PROPERTIES_SEPARATOR)) {
                subscribers.add(subscriber);
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

        StringBuilder urls_property = new StringBuilder();
        rssFeeds.forEach(rssFeed -> {
            urls_property.append(rssFeed.getUrl());
            urls_property.append(PROPERTIES_SEPARATOR);
        });
        properties.setProperty(PROPERTIES_URLS, urls_property.toString());

        StringBuilder subscribers_property = new StringBuilder();
        subscribers.forEach(subscriber -> {
            subscribers_property.append(subscriber);
            subscribers_property.append(PROPERTIES_SEPARATOR);
        });
        properties.setProperty(PROPERTIES_SUBSCRIBERS, subscribers_property.toString());

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
    public String getName() {
        return "RSS_FEED";
    }

    @Override
    public void onLoad(IRCBot bot) {
        // Check RSS feeds on server ping
        bot.addListener(listener);

        bot.addListener(new OnChannelCommandListener() {
            @Override
            public String channelCommand() {
                return ".rss";
            }

            @Override
            public void onChannelCommand(Connection connection, ChannelMessage message, String command, String... args) {
                if (args.length == 1) {
                    PrivateNoticeMessage.Builder builder = new PrivateNoticeMessage.Builder();
                    builder
                            .setSender(connection.getUser())
                            .setReceiver(message.getSender());

                    switch (args[0]) {
                        case "subscribe":
                            subscribers.add(message.getSender().getNick());
                            saveProperties();

                            builder.setText("You has been subscribed to RSS feed!");
                            connection.send(builder.build());

                            break;

                        case "unsubscribe":
                            subscribers.remove(message.getSender().getNick());
                            saveProperties();

                            builder.setText("You has been unsubscribed to RSS feed!");
                            connection.send(builder.build());

                            break;
                    }
                }
            }
        });

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
                            case "subscribers":
                                StringBuilder subscribers_nicks = new StringBuilder();
                                subscribers.forEach(subscriber -> {
                                    subscribers_nicks.append(subscriber);
                                    subscribers_nicks.append(" ");
                                });
                                builder.setText(subscribers_nicks.toString());
                                connection.send(builder.build());

                                break;

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
        PluginHelper.getInstance().addHelp(new PluginHelp(
                ".rss subscribers",
                IRCBotAccess.ADMIN_LEVEL,
                "List all subscribers"));
        PluginHelper.getInstance().addHelp(new PluginHelp(
                ".rss subscribe",
                IRCBotAccess.USER_LEVEL,
                "Subscribe to RSS feed"));
        PluginHelper.getInstance().addHelp(new PluginHelp(
                ".rss unsubscribe",
                IRCBotAccess.USER_LEVEL,
                "Unsubscribe from RSS feed"));
    }

    @Override
    public void onUnload(IRCBot bot) {
        bot.removeListener(listener);
        IRCBotAccess.getInstance().removeListener(bot, ".rss");

        String[] commands = {"check", "list", "add", "del", "subscribers", "subscribe", "unsubscribe"};
        for (String command : commands) {
            PluginHelper.getInstance().removeHelp(".rss " + command);
        }
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

                            subscribers.forEach((subscriber) -> {
                                PrivateNoticeMessage.Builder builder = new PrivateNoticeMessage.Builder();
                                builder
                                        .setSender(connection.getUser())
                                        .setReceiver(new User(subscriber))
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
            worker.setName("RssFeedChecker");
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
