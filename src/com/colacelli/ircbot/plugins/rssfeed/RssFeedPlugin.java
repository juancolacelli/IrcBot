package com.colacelli.ircbot.plugins.rssfeed;

import com.colacelli.ircbot.IRCBot;
import com.colacelli.ircbot.Plugin;
import com.colacelli.irclib.connection.Connection;
import com.colacelli.irclib.connection.listeners.OnJoinListener;
import com.colacelli.irclib.messages.ChannelMessage;

import java.util.ArrayList;

public class RssFeedPlugin implements Plugin {
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
        bot.addListener((OnJoinListener) (connection, user, channel) -> check(connection));
    }

    private void check(Connection connection) {
        for (RssFeed rssFeed : rssFeeds) {
            ArrayList<RssFeedItem> rssFeedItems = rssFeed.check();

            if (!rssFeedItems.isEmpty()) {
                // Use just the first 3 items
                for (int i = 0; i < 3; i++) {
                    RssFeedItem rssFeedItem = rssFeedItems.get(i);

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
        }
    }
}