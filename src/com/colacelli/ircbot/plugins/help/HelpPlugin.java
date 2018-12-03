package com.colacelli.ircbot.plugins.help;

import com.colacelli.ircbot.IRCBot;
import com.colacelli.ircbot.Plugin;
import com.colacelli.ircbot.listeners.OnChannelCommandListener;
import com.colacelli.ircbot.plugins.access.IRCBotAccess;
import com.colacelli.irclib.connection.Connection;
import com.colacelli.irclib.messages.ChannelMessage;
import com.colacelli.irclib.messages.PrivateNoticeMessage;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

public class HelpPlugin implements Plugin {
    private OnChannelCommandListener listener;

    public HelpPlugin() {
        listener = new OnChannelCommandListener() {
            @Override
            public String channelCommand() {
                return ".help";
            }

            @Override
            public void onChannelCommand(Connection connection, ChannelMessage message, String command, String... args) {
                int access = IRCBotAccess.getInstance().getLevel(message.getSender());

                if (args == null) {
                    ArrayList<String> commands = new ArrayList<>();
                    // Getting commands
                    PluginHelper.getInstance().getCommands(access).forEach(text -> commands.add(text.split(" ")[0]));

                    // Removing duplicates
                    Set uniqueCommands = new LinkedHashSet<>(commands);
                    commands.clear();
                    commands.addAll(uniqueCommands);

                    StringBuilder messageText = new StringBuilder();
                    messageText.append("Available commands: (use .help <command> for more information) ");
                    commands.forEach((text) -> {
                        messageText.append(text);
                        messageText.append(" ");
                    });

                    PrivateNoticeMessage.Builder builder = new PrivateNoticeMessage.Builder();
                    builder
                            .setSender(connection.getUser())
                            .setReceiver(message.getSender())
                            .setText(messageText.toString());

                    connection.send(builder.build());
                } else {
                    // Getting full helps
                    PluginHelper.getInstance().getHelp(access, String.join(" ", args)).forEach(text -> {
                        PrivateNoticeMessage.Builder builder = new PrivateNoticeMessage.Builder();
                        builder
                                .setSender(connection.getUser())
                                .setReceiver(message.getSender())
                                .setText(text);

                        connection.send(builder.build());
                    });
                }
            }
        };
    }

    @Override
    public String getName() {
        return "HELP";
    }

    @Override
    public void onLoad(IRCBot bot) {
        bot.addListener(listener);
    }

    @Override
    public void onUnload(IRCBot bot) {
        bot.removeListener(".help");
    }
}
