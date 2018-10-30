package com.colacelli.samplebot.plugins.translator;

import com.colacelli.ircbot.IRCBot;
import com.colacelli.ircbot.plugins.Plugin;
import com.colacelli.irclib.messages.ChannelMessage;
import com.colacelli.samplebot.plugins.help.PluginWithHelp;
import com.colacelli.samplebot.plugins.translator.esperanto.Esperanto;

import java.util.HashMap;
import java.util.Map;

public class TranslatorPlugin implements PluginWithHelp {
    private final Esperanto esperanto = new Esperanto();

    @Override
    public void setup(IRCBot bot) {
        bot.addListener("!eo", (connection, message, command, args) -> {
            if (args != null) {
                String word = args[0];
                HashMap<String, String> translations = esperanto.translate(word);

                if (!translations.isEmpty()) {
                    for (Map.Entry<String, String> entry : translations.entrySet()) {
                        word = entry.getKey();
                        String translation = entry.getValue();

                        ChannelMessage.Builder channelMessageBuilder = new ChannelMessage.Builder();
                        channelMessageBuilder
                                .setSender(connection.getUser())
                                .setChannel(message.getChannel())
                                .setText(word + ": " + translation);

                        connection.send(channelMessageBuilder.build());
                    }
                }
            }
        });
    }

    @Override
    public String[] getHelp() {
        return new String[]{"!eo <word>: Esperanto/English translator"};
    }
}
