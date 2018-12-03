package com.colacelli.ircbot.plugins.thepiratebaysearch;

import com.colacelli.ircbot.IRCBot;
import com.colacelli.ircbot.Plugin;
import com.colacelli.ircbot.listeners.OnChannelCommandListener;
import com.colacelli.ircbot.plugins.help.PluginHelp;
import com.colacelli.ircbot.plugins.help.PluginHelper;
import com.colacelli.irclib.connection.Connection;
import com.colacelli.irclib.messages.ChannelMessage;

import java.util.ArrayList;

public class ThePirateBaySearchPlugin implements Plugin {
    private final OnChannelCommandListener listener;

    public ThePirateBaySearchPlugin() {
        listener = new OnChannelCommandListener() {
            @Override
            public String channelCommand() {
                return ".torrent";
            }

            @Override
            public void onChannelCommand(Connection connection, ChannelMessage message, String command, String... args) {
                if (args != null) {
                    ThePirateBaySearch task = new ThePirateBaySearch(String.join("+", args));

                    task.addListener(new OnThePirateBaySearchResult() {
                        @Override
                        public void onSuccess(ThePirateBaySearchResult result) {
                            ChannelMessage.Builder builder = new ChannelMessage.Builder();
                            builder
                                    .setChannel(message.getChannel())
                                    .setSender(connection.getUser())
                                    .setText(result.toString());

                            connection.send(builder.build());
                        }

                        @Override
                        public void onError(ThePirateBaySearchResult result) {
                            ChannelMessage.Builder builder = new ChannelMessage.Builder();
                            builder
                                    .setChannel(message.getChannel())
                                    .setSender(connection.getUser());

                            if (result == null) {
                                builder.setText("Timeout, please try again!");
                            } else {
                                builder.setText("Torrent not found!");
                            }

                            connection.send(builder.build());
                        }
                    });

                    Thread worker = new Thread(task);
                    worker.setName("ThePirateBaySearch");
                    worker.start();
                }
            }
        };
    }

    @Override
    public String getName() {
        return "TORRENT_PROJECT_SEARCH";
    }

    @Override
    public void onLoad(IRCBot bot) {
        bot.addListener(listener);

        PluginHelper.getInstance().addHelp(new PluginHelp(
                ".torrent",
                "Find torrents on ThePirateBay (https://thepiratebay.online)",
                "query"));
    }

    @Override
    public void onUnload(IRCBot bot) {
        bot.removeListener(".torrent");
        PluginHelper.getInstance().removeHelp(".torrent");
    }

    private class ThePirateBaySearch implements Runnable {
        private ArrayList<OnThePirateBaySearchResult> listeners;
        private String query;

        public ThePirateBaySearch(String query) {
            this.query = query;
            listeners = new ArrayList<>();
        }

        public void addListener(OnThePirateBaySearchResult listener) {
            listeners.add(listener);
        }

        @Override
        public void run() {
            ThePirateBaySearchResult result = ThePirateBaySearchResult.search(query);
            listeners.forEach((listener) -> {
                if (result == null || result.isEmpty()) {
                    listener.onError(result);
                } else {
                    listener.onSuccess(result);
                }
            });
        }
    }
}
