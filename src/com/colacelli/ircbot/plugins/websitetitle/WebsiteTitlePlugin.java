package com.colacelli.ircbot.plugins.websitetitle;

import com.colacelli.ircbot.IRCBot;
import com.colacelli.ircbot.Plugin;
import com.colacelli.irclib.connection.listeners.OnChannelMessageListener;
import com.colacelli.irclib.messages.ChannelMessage;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
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

            // FIXME: Move it into a thread
            while (urlsMatcher.find()) {
                Runnable task = new WebsiteTitleGetter(urlsMatcher.group(0));
                ((WebsiteTitleGetter) task).addListener(new OnWebsiteTitleGetListener() {
                    @Override
                    public void onSuccess(String url, String title) {
                        ChannelMessage.Builder channelMessageBuilder = new ChannelMessage.Builder();
                        channelMessageBuilder
                                .setChannel(message.getChannel())
                                .setSender(connection.getUser())
                                .setText(title + " - " + url);

                        connection.send(channelMessageBuilder.build());
                    }

                    @Override
                    public void onError() {
                    }
                });

                Thread worker = new Thread(task);
                worker.setName("WebsiteTitleGetter");
                worker.start();
            }
        });
    }

    private class WebsiteTitleGetter implements Runnable {
        private String url;
        private ArrayList<OnWebsiteTitleGetListener> websiteTitleGetListeners;

        public WebsiteTitleGetter(String url) {
            this.url = url;
            websiteTitleGetListeners = new ArrayList<>();
        }

        public void addListener(OnWebsiteTitleGetListener listener) {
            websiteTitleGetListeners.add(listener);
        }

        @Override
        public void run() {
            try {
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
                        String finalTitle = title;
                        websiteTitleGetListeners.forEach(listener -> {
                            listener.onSuccess(url, finalTitle);
                        });
                    } catch (IllegalStateException | NoSuchElementException e) {
                        // Title not found
                        websiteTitleGetListeners.forEach(listener -> {
                            listener.onError();
                        });
                    }
                }
            } catch (IOException e) {
                // Invalid URL
                websiteTitleGetListeners.forEach(listener -> listener.onError());
            }
        }
    }
}
