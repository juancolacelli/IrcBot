package com.colacelli.ircbot.plugins.operator;

import com.colacelli.ircbot.IRCBot;
import com.colacelli.ircbot.Plugin;
import com.colacelli.ircbot.listeners.OnChannelCommandListener;
import com.colacelli.ircbot.plugins.access.IRCBotAccess;
import com.colacelli.ircbot.plugins.help.PluginHelp;
import com.colacelli.ircbot.plugins.help.PluginHelper;
import com.colacelli.irclib.actors.User;
import com.colacelli.irclib.connection.Connection;
import com.colacelli.irclib.messages.ChannelMessage;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class OperatorPlugin implements Plugin {
    private HashMap<String, String> commandModes;

    public OperatorPlugin() {
        commandModes = new HashMap<>();
        commandModes.put(".owner", "+q");
        commandModes.put(".deowner", "-q");
        commandModes.put(".protect", "+a");
        commandModes.put(".deprotect", "-a");
        commandModes.put(".op", "+o");
        commandModes.put(".deop", "-o");
        commandModes.put(".halfop", "+h");
        commandModes.put(".dehalfop", "-h");
        commandModes.put(".voice", "+v");
        commandModes.put(".devoice", "-v");
    }

    @Override
    public String getName() {
        return "OPERATOR";
    }

    @Override
    public void onLoad(IRCBot bot) {
        for (Map.Entry<String, String> entry : commandModes.entrySet()) {
            String text = entry.getKey();
            String mode = entry.getValue();

            IRCBotAccess.getInstance().addListener(bot, IRCBotAccess.OPERATOR_LEVEL, new OnChannelCommandListener() {
                @Override
                public String channelCommand() {
                    return text;
                }

                @Override
                public void onChannelCommand(Connection connection, ChannelMessage message, String command, String... args) {
                    StringBuilder nicks = new StringBuilder();
                    StringBuilder modes = new StringBuilder();

                    if (args != null) {
                        for (String nick : args) {
                            if (mode.contains("+") || !connection.getUser().getNick().toLowerCase().equals(nick.toLowerCase())) {
                                nicks.append(nick);
                                nicks.append(" ");
                                modes.append(mode);
                            }
                        }
                    } else {
                        nicks.append(message.getSender().getNick());
                        modes.append(mode);
                    }

                    connection.mode(message.getChannel(), modes.toString() + " " + nicks.toString());
                }
            });

            PluginHelper.getInstance().addHelp(new PluginHelp(
                    text,
                    IRCBotAccess.OPERATOR_LEVEL,
                    mode + " user channel mode",
                    "user"));
        }

        IRCBotAccess.getInstance().addListener(bot, IRCBotAccess.OPERATOR_LEVEL, new OnChannelCommandListener() {
            @Override
            public String channelCommand() {
                return ".mode";
            }

            @Override
            public void onChannelCommand(Connection connection, ChannelMessage message, String command, String... args) {
                if (args != null) {
                    String modes = String.join(" ", args);
                    connection.mode(message.getChannel(), modes);
                }
            }
        });
        PluginHelper.getInstance().addHelp(new PluginHelp(
                ".mode",
                IRCBotAccess.OPERATOR_LEVEL,
                "Changes channel modes",
                "modes"));

        IRCBotAccess.getInstance().addListener(bot, IRCBotAccess.OPERATOR_LEVEL, new OnChannelCommandListener() {
            @Override
            public String channelCommand() {
                return ".invite";
            }

            @Override
            public void onChannelCommand(Connection connection, ChannelMessage message, String command, String... args) {
                if (args != null) {
                    connection.invite(message.getChannel(), new User(args[0]));
                }
            }
        });
        PluginHelper.getInstance().addHelp(new PluginHelp(
                ".invite",
                IRCBotAccess.OPERATOR_LEVEL,
                "Invites a user to channel",
                "nick"));

        IRCBotAccess.getInstance().addListener(bot, IRCBotAccess.OPERATOR_LEVEL, new OnChannelCommandListener() {
            @Override
            public String channelCommand() {
                return ".kick";
            }

            @Override
            public void onChannelCommand(Connection connection, ChannelMessage message, String command, String... args) {
                if (args != null) {
                    String nick = args[0];

                    if (!connection.getUser().getNick().toLowerCase().equals(nick.toLowerCase())) {
                        String reason = args.length > 1 ? String.join(" ", Arrays.copyOfRange(args, 1, args.length)) : "...";
                        connection.kick(message.getChannel(), new User(nick), reason);
                    }
                }
            }
        });
        PluginHelper.getInstance().addHelp(new PluginHelp(
                ".kick",
                IRCBotAccess.OPERATOR_LEVEL,
                "Kicks a user from channel",
                "nick",
                "reason"));

        IRCBotAccess.getInstance().addListener(bot, IRCBotAccess.OPERATOR_LEVEL, new OnChannelCommandListener() {
            @Override
            public String channelCommand() {
                return ".ban";
            }

            @Override
            public void onChannelCommand(Connection connection, ChannelMessage message, String command, String... args) {
                if (args != null) {
                    String nick = args[0];

                    if (!connection.getUser().getNick().toLowerCase().equals(nick.toLowerCase())) {
                        StringBuilder ban = new StringBuilder();
                        ban.append("+b ");
                        ban.append(nick);
                        ban.append("!*@*");
                        connection.mode(message.getChannel(), ban.toString());
                    }
                }
            }
        });
        PluginHelper.getInstance().addHelp(new PluginHelp(
                ".ban",
                IRCBotAccess.OPERATOR_LEVEL,
                "Bans a user from channel",
                "nick"));

        IRCBotAccess.getInstance().addListener(bot, IRCBotAccess.OPERATOR_LEVEL, new OnChannelCommandListener() {
            @Override
            public String channelCommand() {
                return ".unban";
            }

            @Override
            public void onChannelCommand(Connection connection, ChannelMessage message, String command, String... args) {
                if (args != null) {
                    String nick = args[0];
                    StringBuilder unban = new StringBuilder();
                    unban.append("-b ");
                    unban.append(nick);
                    unban.append("!*@*");
                    connection.mode(message.getChannel(), unban.toString());
                }
            }
        });
        PluginHelper.getInstance().addHelp(new PluginHelp(
                ".unban",
                IRCBotAccess.OPERATOR_LEVEL,
                "Removes a ban from channel",
                "nick"));

        IRCBotAccess.getInstance().addListener(bot, IRCBotAccess.OPERATOR_LEVEL, new OnChannelCommandListener() {
            @Override
            public String channelCommand() {
                return ".kickban";
            }

            @Override
            public void onChannelCommand(Connection connection, ChannelMessage message, String command, String... args) {
                if (args != null) {
                    String nick = args[0];

                    if (!connection.getUser().getNick().toLowerCase().equals(nick.toLowerCase())) {
                        String reason = args.length > 1 ? String.join(" ", Arrays.copyOfRange(args, 1, args.length)) : "...";
                        StringBuilder ban = new StringBuilder();
                        ban.append("+b ");
                        ban.append(nick);
                        ban.append("!*@*");
                        connection.mode(message.getChannel(), ban.toString());
                        connection.kick(message.getChannel(), new User(nick), reason);
                    }
                }
            }
        });
        PluginHelper.getInstance().addHelp(new PluginHelp(
                ".kickban",
                IRCBotAccess.OPERATOR_LEVEL,
                "Kicks and bans a user from channel",
                "nick",
                "reason"));

        IRCBotAccess.getInstance().addListener(bot, IRCBotAccess.OPERATOR_LEVEL, new OnChannelCommandListener() {
            @Override
            public String channelCommand() {
                return ".topic";
            }

            @Override
            public void onChannelCommand(Connection connection, ChannelMessage message, String command, String... args) {
                if (args != null) {
                    String topic = String.join(" ", args);
                    connection.topic(message.getChannel(), topic);
                }
            }
        });
        PluginHelper.getInstance().addHelp(new PluginHelp(
                ".topic",
                IRCBotAccess.OPERATOR_LEVEL,
                "Changes channel topic",
                "topic"));
    }

    @Override
    public void onUnload(IRCBot bot) {
        for (Map.Entry<String, String> entry : commandModes.entrySet()) {
            String command = entry.getKey();
            PluginHelper.getInstance().removeHelp(command);
            IRCBotAccess.getInstance().removeListener(bot, command);
        }

        String[] commands = {".mode", ".kick", ".topic", ".ban", ".kickban", ".unban"};
        for (String command : commands) {
            PluginHelper.getInstance().removeHelp(command);
            IRCBotAccess.getInstance().removeListener(bot, command);
        }
    }
}
