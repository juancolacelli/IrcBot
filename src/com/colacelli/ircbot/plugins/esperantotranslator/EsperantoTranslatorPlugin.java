package com.colacelli.ircbot.plugins.esperantotranslator;

import com.colacelli.ircbot.IRCBot;
import com.colacelli.ircbot.plugins.help.PluginWithHelp;
import com.colacelli.irclib.messages.ChannelMessage;
import com.colacelli.ircbot.plugins.esperantotranslator.esperanto.EsperantoTranslator;

import java.util.ArrayList;

public class EsperantoTranslatorPlugin implements PluginWithHelp {
    private final String revoPath;
    private final EsperantoTranslator esperantoTranslator;

    public EsperantoTranslatorPlugin(String revoPath) {
        this.revoPath = revoPath;
        this.esperantoTranslator = EsperantoTranslator.getInstance();
        this.esperantoTranslator.load(revoPath);
    }

    @Override
    public void setup(IRCBot bot) {
        bot.addListener("!translate", (connection, message, command, args) -> {
            if (args != null && args.length > 1) {
                // Esperanto
                if (args[0].indexOf("eo") > -1) {
                    if (esperantoTranslator.isLoaded()) {
                        String locale = args[0];
                        String word = args[1];

                        ArrayList<String> translations = esperantoTranslator.translate(locale, word);

                        if (translations != null && !translations.isEmpty()) {
                            for (String translation : translations) {
                                String[] locales = locale.split("-");

                                ChannelMessage.Builder channelMessageBuilder = new ChannelMessage.Builder();
                                channelMessageBuilder
                                        .setSender(connection.getUser())
                                        .setChannel(message.getChannel())
                                        .setText("[" + locales[0] + "] " + word + " ~ " + "[" + locales[1] + "] " + translation);

                                connection.send(channelMessageBuilder.build());
                            }
                        }
                    } else {
                        ChannelMessage.Builder channelMessageBuilder = new ChannelMessage.Builder();
                        channelMessageBuilder
                                .setSender(connection.getUser())
                                .setChannel(message.getChannel())
                                .setText("Loading Esperanto translations, please try again later...");

                        connection.send(channelMessageBuilder.build());
                    }
                }
            }
        });
    }

    @Override
    public String[] getHelp() {
        return new String[]{"!translate <eo-locale/locale-eo> <word>: Translator"};
    }
}
