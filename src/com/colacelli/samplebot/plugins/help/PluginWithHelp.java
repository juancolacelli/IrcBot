package com.colacelli.samplebot.plugins.help;

import com.colacelli.ircbot.plugins.Plugin;

public interface PluginWithHelp extends Plugin {
    String[] getHelp();
}
