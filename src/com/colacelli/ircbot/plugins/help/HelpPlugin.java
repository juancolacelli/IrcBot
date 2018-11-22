package com.colacelli.ircbot.plugins.help;

import com.colacelli.ircbot.IRCBot;
import com.colacelli.ircbot.Plugin;
import com.colacelli.ircbot.plugins.access.IRCBotAccess;
import com.colacelli.irclib.messages.PrivateNoticeMessage;

public class HelpPlugin implements Plugin {
    @Override
    public void setup(IRCBot bot) {
        bot.addListener(".help", (connection, message, command, args) -> {
            int access = IRCBotAccess.getInstance().getLevel(message.getSender());

            PluginHelper.getInstance().getHelp(access).forEach(text -> {
                PrivateNoticeMessage.Builder builder = new PrivateNoticeMessage.Builder();
                builder
                        .setSender(connection.getUser())
                        .setReceiver(message.getSender())
                        .setText(text);

                connection.send(builder.build());
            });
        });
    }
}
