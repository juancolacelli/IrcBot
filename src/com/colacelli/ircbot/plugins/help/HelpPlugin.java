package com.colacelli.ircbot.plugins.help;

import com.colacelli.ircbot.IRCBot;
import com.colacelli.ircbot.Plugin;
import com.colacelli.ircbot.listeners.OnChannelCommandListener;
import com.colacelli.ircbot.plugins.access.IRCBotAccess;
import com.colacelli.irclib.connection.Connection;
import com.colacelli.irclib.messages.ChannelMessage;
import com.colacelli.irclib.messages.PrivateNoticeMessage;

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

                PluginHelper.getInstance().getHelp(access).forEach(text -> {
                    PrivateNoticeMessage.Builder builder = new PrivateNoticeMessage.Builder();
                    builder
                            .setSender(connection.getUser())
                            .setReceiver(message.getSender())
                            .setText(text);

                    connection.send(builder.build());
                });
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
