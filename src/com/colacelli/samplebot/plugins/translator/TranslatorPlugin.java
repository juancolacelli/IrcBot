package com.colacelli.samplebot.plugins.translator;

import com.colacelli.ircbot.IRCBot;
import com.colacelli.ircbot.plugins.Plugin;
import com.colacelli.irclib.messages.ChannelMessage;
import com.colacelli.samplebot.plugins.translator.esperanto.Esperanto;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TranslatorPlugin implements Plugin {
    private final Esperanto esperanto = new Esperanto();

    @Override
    public void setup(IRCBot bot) {
        bot.addListener("!eo", (connection, message, command, args) -> {
            String word = args[0];
            HashMap<String, String> translations = esperanto.translate(word);

            if (!translations.isEmpty()) {
                for(Map.Entry<String, String> entry : translations.entrySet()) {
                    word = entry.getKey();
                    String translation = entry.getValue();

                    ChannelMessage.Builder channelMessageBuilder = new ChannelMessage.Builder();
                    channelMessageBuilder
                            .setSender(connection.getUser())
                            .setChannel(message.getChannel())
                            .setText(word + ": " + translation);

                    try {
                        connection.send(channelMessageBuilder.build());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
