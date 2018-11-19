package com.colacelli.ircbot.plugins.help;

public class PluginHelp {
    private String command;
    private int access;
    private String help;
    private String[] args;

    public PluginHelp(String command, int access, String help, String... args) {
        this.command = command;
        this.access = access;
        this.help = help;
        this.args = args;
    }

    public PluginHelp(String command, String help, String... args) {
        this.command = command;
        this.access = 0;
        this.help = help;
        this.args = args;
    }

    public int getAccess() {
        return access;
    }

    @Override
    public String toString() {
        StringBuilder text = new StringBuilder();
        text.append(command);

        for (String arg : args) {
            text.append(" <");
            text.append(arg);
            text.append(">");
        }

        text.append(": ");
        text.append(help);

        return text.toString();
    }
}
