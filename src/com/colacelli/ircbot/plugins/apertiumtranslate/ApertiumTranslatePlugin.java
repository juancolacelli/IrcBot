package com.colacelli.ircbot.plugins.apertiumtranslate;

import com.colacelli.ircbot.IRCBot;
import com.colacelli.ircbot.plugins.help.PluginWithHelp;
import com.colacelli.irclib.messages.ChannelMessage;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

public class ApertiumTranslatePlugin implements PluginWithHelp {
    private static final String APERTIUM_URL = "https://www.apertium.org/apy/translate?q=TEXT&langpair=LOCALES";
    private static final String APERTIUM_JSON_DATA = "responseData";
    private static final String APERTIUM_JSON_TRANSLATION = "translatedText";

    @Override
    public String[] getHelp() {
        return new String[]{
                "!translate <locale A> <locale B> <text>: Translate text from locale A to locale B using Apertium (https://apertium.org)"
        };
    }

    @Override
    public void setup(IRCBot bot) {
        bot.addListener("!translate", (connection, message, command, args) -> {
            if (args != null && args.length > 2) {
                String localeA = args[0];
                String localeB = args[1];
                StringBuilder text = new StringBuilder();

                text.append(args[2]);

                for (int i = 3; i < args.length; i++) {
                    text.append(" ");
                    text.append(args[i]);
                }

                // FIXME: Move it into a thread
                String translation = getTranslation(localeA, localeB, text.toString());

                if (translation == null || translation.isEmpty()) {
                    translation = "Translation not found!";
                }

                ChannelMessage.Builder channelMessageBuilder = new ChannelMessage.Builder();
                channelMessageBuilder
                        .setChannel(message.getChannel())
                        .setSender(connection.getUser())
                        .setText("[" + localeA + "] " + text + " ~ " + "[" + localeB + "] " + translation);

                connection.send(channelMessageBuilder.build());
            }
        });
    }

    private String getTranslation(String localeA, String localeB, String text) {
        String url = APERTIUM_URL
                .replace("LOCALES", localeA + "|" + localeB)
                .replace("TEXT", text.replaceAll(" ", "%20"));

        String translation = null;

        try {
            StringBuilder jsonText = new StringBuilder();

            InputStream response = new URL(url).openStream();
            Scanner scanner = new Scanner(response).useDelimiter("\\A");
            while (scanner.hasNext()) {
                jsonText.append(scanner.next());
            }

            JSONObject json = (JSONObject) new JSONParser().parse(jsonText.toString());
            json = (JSONObject) json.get(APERTIUM_JSON_DATA);

            translation = (String) json.get(APERTIUM_JSON_TRANSLATION);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return translation;
    }
}