package com.colacelli.ircbot.plugins.websitetitle;

import com.colacelli.ircbot.IRCBot;
import com.colacelli.ircbot.Plugin;
import com.colacelli.irclib.connection.listeners.OnChannelMessageListener;
import com.colacelli.irclib.messages.ChannelMessage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebsiteTitlePlugin implements Plugin {
    private OnChannelMessageListener listener;

    @Override
    public String getName() {
        return "WEBSITE_TITLE";
    }

    @Override
    public void onLoad(IRCBot bot) {
        listener = (connection, message) -> {
            String text = message.getText();
            Pattern urlsPattern = Pattern.compile("((http://|https://)([^ ]+))");
            Matcher urlsMatcher = urlsPattern.matcher(text);

            while (urlsMatcher.find()) {
                WebsiteTitleGetter task = new WebsiteTitleGetter(urlsMatcher.group(0));
                task.addListener(new OnWebsiteTitleGetListener() {
                    @Override
                    public void onSuccess(String url, String title) {
                        ChannelMessage.Builder builder = new ChannelMessage.Builder();
                        builder
                                .setChannel(message.getChannel())
                                .setSender(connection.getUser())
                                .setText(title + " - " + url);

                        connection.send(builder.build());
                    }

                    @Override
                    public void onError() {
                    }
                });

                Thread worker = new Thread(task);
                worker.setName("WebsiteTitleGetter");
                worker.start();
            }
        };

        bot.addListener(listener);
    }

    @Override
    public void onUnload(IRCBot bot) {
        bot.removeListener(listener);
    }

    private class WebsiteTitleGetter implements Runnable {
        private String url;
        private ArrayList<OnWebsiteTitleGetListener> onWebsiteTitleGetListeners;

        public WebsiteTitleGetter(String url) {
            this.url = url;
            onWebsiteTitleGetListeners = new ArrayList<>();
        }

        public void addListener(OnWebsiteTitleGetListener listener) {
            onWebsiteTitleGetListeners.add(listener);
        }

        @Override
        public void run() {
            try {
                Document document = Jsoup.connect(url).get();
                String title = document.title();
                onWebsiteTitleGetListeners.forEach(listener -> listener.onSuccess(url, title));
            } catch (IOException e) {
                // Invalid URL
                onWebsiteTitleGetListeners.forEach(OnWebsiteTitleGetListener::onError);
            }
        }
    }
}
