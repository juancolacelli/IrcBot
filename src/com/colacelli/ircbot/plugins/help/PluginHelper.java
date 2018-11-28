package com.colacelli.ircbot.plugins.help;

import java.util.ArrayList;

public class PluginHelper {
    private static PluginHelper instance;
    private ArrayList<PluginHelp> helps;

    private PluginHelper() {
        helps = new ArrayList<>();
    }

    public static PluginHelper getInstance() {
        if (instance == null) instance = new PluginHelper();
        return instance;
    }

    public void addHelp(PluginHelp help) {
        helps.add(help);
    }

    public void removeHelp(String command) {
        for (int i = 0; i < helps.size(); i++) {
            PluginHelp help = helps.get(i);
            if (help.getCommand().toUpperCase().equals(command.toUpperCase())) helps.remove(help);
        }
    }

    public ArrayList<String> getHelp(int access) {
        ArrayList<String> helpTexts = new ArrayList<>();
        this.helps.forEach(help -> {
            if (help.getAccess() <= access) {
                helpTexts.add(help.toString());
            }
        });

        return helpTexts;
    }

    public ArrayList<String> getHelp() {
        return getHelp(0);
    }
}