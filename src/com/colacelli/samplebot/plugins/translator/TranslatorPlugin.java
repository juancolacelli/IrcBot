package com.colacelli.samplebot.plugins.translator;

import com.colacelli.ircbot.IRCBot;
import com.colacelli.irclib.messages.ChannelMessage;
import com.colacelli.samplebot.plugins.help.PluginWithHelp;
import com.colacelli.samplebot.plugins.translator.esperanto.EsperantoTranslator;

public class TranslatorPlugin implements PluginWithHelp {
    private final EsperantoTranslator esperantoTranslator = EsperantoTranslator.getInstance();

    @Override
    public void setup(IRCBot bot) {
        bot.addListener("!translate", (connection, message, command, args) -> {
            if (args != null && args.length > 1) {
                // Esperanto
                if (args[0].indexOf("eo") > -1) {
                    if (esperantoTranslator.isLoaded()) {
                        String locale = args[0];
                        String word = args[1];

                        String translation = esperantoTranslator.translate(locale, word);

                        if (translation != null && !translation.isEmpty()) {
                            String[] locales = locale.split("-");

                            ChannelMessage.Builder channelMessageBuilder = new ChannelMessage.Builder();
                            channelMessageBuilder
                                    .setSender(connection.getUser())
                                    .setChannel(message.getChannel())
                                    .setText("[" + locales[0] + "] " + word + " ~ " + "[" + locales[1] + "] " + translation);

                            connection.send(channelMessageBuilder.build());
                        }
                    } else {
                        ChannelMessage.Builder channelMessageBuilder = new ChannelMessage.Builder();
                        channelMessageBuilder
                                .setSender(connection.getUser())
                                .setChannel(message.getChannel())
                                .setText("Loading Esperanto dictionary, please try again later...");

                        connection.send(channelMessageBuilder.build());
                    }
                }
            }
        });
    }

    @Override
    public String[] getHelp() {
        return new String[]{"!translate <locale-locale> <word>: Translator"};
    }
}
