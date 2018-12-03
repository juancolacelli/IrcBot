package com.colacelli.ircbot.plugins.help;

import java.util.ArrayList;
import java.util.Comparator;

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

    public ArrayList<String> getCommands(int access) {
        ArrayList<String> helpTexts = new ArrayList<>();
        this.helps.sort(Comparator.comparing(PluginHelp::getCommand));
        this.helps.forEach(help -> {
            if (help.getAccess() <= access) {
                helpTexts.add(help.getCommand());
            }
        });

        return helpTexts;
    }

    public ArrayList<String> getHelp(int access, String command) {
        ArrayList<String> helpTexts = new ArrayList<>();
        this.helps.sort(Comparator.comparing(PluginHelp::getCommand));
        this.helps.forEach(help -> {
            if (help.getAccess() <= access && (help.getCommand().startsWith(command.toLowerCase()) || help.getCommand().startsWith("." + command.toLowerCase()))) {
                helpTexts.add(help.toString());
            }
        });

        return helpTexts;
    }
}
