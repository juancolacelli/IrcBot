package com.colacelli.ircbot.plugins.help;

import com.colacelli.ircbot.Plugin;

public interface PluginWithHelp extends Plugin {
    String[] getHelp();
}
