package com.colacelli.ircbot.plugins.autoresponse;

import com.colacelli.ircbot.IRCBot;
import com.colacelli.ircbot.Plugin;
import com.colacelli.ircbot.listeners.OnChannelCommandListener;
import com.colacelli.ircbot.plugins.access.IRCBotAccess;
import com.colacelli.ircbot.plugins.help.PluginHelp;
import com.colacelli.ircbot.plugins.help.PluginHelper;
import com.colacelli.irclib.connection.Connection;
import com.colacelli.irclib.connection.listeners.OnChannelMessageListener;
import com.colacelli.irclib.messages.ChannelMessage;
import com.colacelli.irclib.messages.PrivateNoticeMessage;

import java.util.Arrays;
import java.util.HashMap;

public class AutoResponsePlugin implements Plugin {
    OnChannelMessageListener listener;

    public AutoResponsePlugin() {
        this.listener = (connection, message) -> {
            String autoResponse = AutoResponse.getInstance().getAutoResponse(message);

            if (autoResponse != null && !autoResponse.isEmpty()) {
                ChannelMessage.Builder builder = new ChannelMessage.Builder();
                builder
                        .setSender(connection.getUser())
                        .setChannel(message.getChannel())
                        .setText(autoResponse);

                connection.send(builder.build());
            }
        };
    }

    @Override
    public String getName() {
        return "AUTO_RESPONSE";
    }

    @Override
    public void onLoad(IRCBot bot) {
        bot.addListener(listener);

        IRCBotAccess.getInstance().addListener(bot, IRCBotAccess.OPERATOR_LEVEL, new OnChannelCommandListener() {
            @Override
            public String channelCommand() {
                return ".autoresponse";
            }

            @Override
            public void onChannelCommand(Connection connection, ChannelMessage message, String command, String... args) {
                if (args != null) {
                    PrivateNoticeMessage.Builder builder = new PrivateNoticeMessage.Builder();
                    builder
                            .setSender(connection.getUser())
                            .setReceiver(message.getSender());

                    if (args.length > 1) {
                        switch (args[0]) {
                            case "add":
                                try {
                                    String messageText = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
                                    String trigger = messageText.substring(0, messageText.indexOf(AutoResponsePluginHelp.SEPARATOR));
                                    String response = messageText.substring(messageText.indexOf(AutoResponsePluginHelp.SEPARATOR) + 1);
                                    AutoResponse.getInstance().setAutoResponse(trigger, response);

                                    builder.setText("Autoresponse added!");
                                } catch (StringIndexOutOfBoundsException e) {
                                    // Separator not found!
                                    builder.setText("Error parsing trigger/response, please try again!");
                                }
                                connection.send(builder.build());
                                break;

                            case "del":
                                AutoResponse.getInstance().setAutoResponse(args[1], "");

                                builder.setText("Autoresponse removed!");
                                connection.send(builder.build());
                                break;
                        }
                    } else {
                        switch (args[0]) {
                            case "list":
                                HashMap<String, String> autoResponses = AutoResponse.getInstance().getAutoResponses();
                                autoResponses.forEach((trigger, response) -> {
                                    builder.setText(trigger + ": " + response);
                                    connection.send(builder.build());
                                });
                                break;
                        }
                    }
                }
            }
        });

        PluginHelper.getInstance().addHelp(new AutoResponsePluginHelp(
                ".autoresponse add",
                IRCBotAccess.OPERATOR_LEVEL,
                "Adds an autoresponse. Available replacements: $nick and $channel, ie., .autoresponse add hello" + AutoResponsePluginHelp.SEPARATOR + "hello $nick, welcome to $channel!",
                "trigger",
                "response"));

        PluginHelper.getInstance().addHelp(new PluginHelp(
                ".autoresponse del",
                IRCBotAccess.OPERATOR_LEVEL,
                "Removes an autoresponse",
                "trigger"));

        PluginHelper.getInstance().addHelp(new PluginHelp(
                ".autoresponse list",
                IRCBotAccess.OPERATOR_LEVEL,
                "List all autoresponses"));
    }

    @Override
    public void onUnload(IRCBot bot) {
        bot.removeListener(listener);
        bot.removeListener(".autoresponse");

        String[] commands = {"list", "add", "del"};
        for (String command : commands) {
            PluginHelper.getInstance().removeHelp(".autoresponse " + command);
        }
    }
}
