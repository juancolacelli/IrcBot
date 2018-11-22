package com.colacelli.ircbot.plugins.ctcpversion;

import com.colacelli.ircbot.IRCBot;
import com.colacelli.ircbot.Plugin;
import com.colacelli.irclib.connection.listeners.OnCtcpListener;
import com.colacelli.irclib.messages.CTCPMessage;

public class CTCPVersionPlugin implements Plugin {
    String version;

    public CTCPVersionPlugin(String version) {
        this.version = version;
    }

    @Override
    public void setup(IRCBot bot) {
        bot.addListener((OnCtcpListener) (connection, message, args1) -> {
            switch (message.getCommand()) {
                case "VERSION":
                    CTCPMessage.Builder builder = new CTCPMessage.Builder();
                    builder
                            .setSender(connection.getUser())
                            .setReceiver(message.getSender())
                            .setText(version);

                    builder.setCommand(message.getCommand());

                    connection.send(builder.build());
                    break;
            }
        });
    }
}
