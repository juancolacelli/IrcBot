package com.colacelli.ircbot.plugins.apertiumtranslate;

import com.colacelli.ircbot.IRCBot;
import com.colacelli.ircbot.Plugin;
import com.colacelli.ircbot.listeners.OnChannelCommandListener;
import com.colacelli.ircbot.plugins.access.IRCBotAccess;
import com.colacelli.ircbot.plugins.help.PluginHelp;
import com.colacelli.ircbot.plugins.help.PluginHelper;
import com.colacelli.irclib.connection.Connection;
import com.colacelli.irclib.messages.ChannelMessage;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

public class ApertiumTranslatePlugin implements Plugin {
    private static final String APERTIUM_URL = "https://www.apertium.org/apy/translate?q=TEXT&langpair=LOCALES";
    private static final String APERTIUM_JSON_DATA = "responseData";
    private static final String APERTIUM_JSON_TRANSLATION = "translatedText";

    private OnChannelCommandListener listener;

    public ApertiumTranslatePlugin() {
        listener = new OnChannelCommandListener() {
            @Override
            public String channelCommand() {
                return ".translate";
            }

            @Override
            public void onChannelCommand(Connection connection, ChannelMessage message, String command, String... args) {
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

                    ChannelMessage.Builder builder = new ChannelMessage.Builder();
                    builder
                            .setChannel(message.getChannel())
                            .setSender(connection.getUser())
                            .setText("[" + localeA + "] " + text + " ~ " + "[" + localeB + "] " + translation);

                    connection.send(builder.build());
                }
            }
        };
    }

    @Override
    public String getName() {
        return "APERTIUM_TRANSLATE";
    }

    @Override
    public void onLoad(IRCBot bot) {
        bot.addListener(listener);

        PluginHelper.getInstance().addHelp(new PluginHelp(
                ".translate",
                IRCBotAccess.USER_LEVEL,
                "Translate text from locale1 to locale2 using Apertium (https://apertium.org)",
                "locale1",
                "locale2"));
    }

    @Override
    public void onUnload(IRCBot bot) {
        bot.removeListener(".translate");
        PluginHelper.getInstance().removeHelp(".translate");
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
