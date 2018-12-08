package com.colacelli.ircbot.plugins.autoresponse;

import com.colacelli.ircbot.plugins.help.PluginHelp;

public class AutoResponsePluginHelp extends PluginHelp {
    static final String SEPARATOR = "|";

    public AutoResponsePluginHelp(String command, int access, String help, String... args) {
        super(command, access, help, args);
    }

    @Override
    public String toString() {
        StringBuilder text = new StringBuilder();
        text.append(command);
        text.append(" ");

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];

            if (i != 0) text.append(SEPARATOR);

            text.append("<");
            text.append(arg);
            text.append(">");
        }

        text.append(": ");
        text.append(help);

        return text.toString();
    }
}
