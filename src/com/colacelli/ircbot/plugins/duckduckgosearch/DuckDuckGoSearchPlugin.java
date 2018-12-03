package com.colacelli.ircbot.plugins.duckduckgosearch;

import com.colacelli.ircbot.IRCBot;
import com.colacelli.ircbot.Plugin;
import com.colacelli.ircbot.listeners.OnChannelCommandListener;
import com.colacelli.ircbot.plugins.help.PluginHelp;
import com.colacelli.ircbot.plugins.help.PluginHelper;
import com.colacelli.irclib.connection.Connection;
import com.colacelli.irclib.messages.ChannelMessage;

import java.util.ArrayList;

public class DuckDuckGoSearchPlugin implements Plugin {
    private final OnChannelCommandListener listener;

    public DuckDuckGoSearchPlugin() {
        listener = new OnChannelCommandListener() {
            @Override
            public String channelCommand() {
                return ".ddgo";
            }

            @Override
            public void onChannelCommand(Connection connection, ChannelMessage message, String command, String... args) {
                if (args != null) {
                    DuckDuckGoSearch task = new DuckDuckGoSearch(String.join("%20", args));

                    task.addListener(new OnDuckDuckGoSearchResultListener() {
                        @Override
                        public void onSuccess(DuckDuckGoSearchResult searchResult) {
                            ChannelMessage.Builder builder = new ChannelMessage.Builder();
                            builder
                                    .setChannel(message.getChannel())
                                    .setSender(connection.getUser())
                                    .setText(searchResult.toString());

                            connection.send(builder.build());
                        }

                        @Override
                        public void onError() {
                            ChannelMessage.Builder builder = new ChannelMessage.Builder();
                            builder
                                    .setChannel(message.getChannel())
                                    .setSender(connection.getUser())
                                    .setText("Query not found!");

                            connection.send(builder.build());
                        }
                    });

                    Thread worker = new Thread(task);
                    worker.setName("DuckDuckGoSearch");
                    worker.start();
                }
            }
        };
    }

    @Override
    public String getName() {
        return "DUCK_DUCK_GO_SEARCH";
    }

    @Override
    public void onLoad(IRCBot bot) {
        bot.addListener(listener);

        PluginHelper.getInstance().addHelp(new PluginHelp(
                ".ddgo",
                "Find on DuckDuckGo (https://duckduckgo.com)",
                "query"));
    }

    @Override
    public void onUnload(IRCBot bot) {
        bot.removeListener(".ddgo");
        PluginHelper.getInstance().removeHelp(".ddgo");
    }

    private class DuckDuckGoSearch implements Runnable {
        private ArrayList<OnDuckDuckGoSearchResultListener> listeners;
        private String query;

        public DuckDuckGoSearch(String query) {
            this.query = query;
            listeners = new ArrayList<>();
        }

        public void addListener(OnDuckDuckGoSearchResultListener listener) {
            listeners.add(listener);
        }

        @Override
        public void run() {
            DuckDuckGoSearchResult result = DuckDuckGoSearchResult.get(query);
            listeners.forEach((listener) -> {
                if (result.isEmpty()) {
                    listener.onError();
                } else {
                    listener.onSuccess(result);
                }
            });
        }
    }
}
