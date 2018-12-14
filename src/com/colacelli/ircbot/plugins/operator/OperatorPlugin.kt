package com.colacelli.ircbot.plugins.operator

import com.colacelli.ircbot.IRCBot
import com.colacelli.ircbot.Plugin
import com.colacelli.ircbot.listeners.OnChannelCommandListener
import com.colacelli.ircbot.plugins.access.IRCBotAccess
import com.colacelli.ircbot.plugins.help.PluginHelp
import com.colacelli.ircbot.plugins.help.PluginHelper
import com.colacelli.irclib.actors.User
import com.colacelli.irclib.connection.Connection
import com.colacelli.irclib.messages.ChannelMessage

class OperatorPlugin : Plugin {
    private val modes = hashMapOf(
            "owner" to "q",
            "protect" to "a",
            "op" to "o",
            "halfop" to "h",
            "voice" to "v"
    )
    private val modifiers = hashMapOf(
            "" to "+",
            "de" to "-"
    )

    override fun getName(): String {
        return "operator"
    }

    override fun onLoad(bot: IRCBot) {
        modifiers.forEach { prefix, sign ->
            modes.forEach { name, mode ->
                IRCBotAccess.instance.addListener(bot, IRCBotAccess.Level.OPERATOR, object : OnChannelCommandListener {
                    override fun channelCommand(): String {
                        return ".$prefix$name"
                    }

                    override fun onChannelCommand(connection: Connection, message: ChannelMessage, command: String, args: Array<String>) {
                        if (args.isNotEmpty()) {
                            var nicks = ""
                            var modes = ""
                            args.forEach {
                                if (it.toLowerCase() != connection.user.nick.toLowerCase()) {
                                    modes += "$sign$mode"
                                    nicks += "$it "
                                }
                            }

                            connection.mode(message.channel, "$modes $nicks")
                        } else {
                            connection.mode(message.channel, "$sign$mode ${message.sender?.nick}")
                        }
                    }
                })

                PluginHelper.instance.addHelp(PluginHelp(
                        ".$prefix$name",
                        IRCBotAccess.Level.OPERATOR,
                        "$sign$mode user channel mode",
                        "nick1",
                        "nick2"))
            }
        }

        IRCBotAccess.instance.addListener(bot, IRCBotAccess.Level.OPERATOR, object : OnChannelCommandListener {
            override fun channelCommand(): String {
                return ".k"
            }

            override fun onChannelCommand(connection: Connection, message: ChannelMessage, command: String, args: Array<String>) {
                if (args.isNotEmpty()) {
                    val nick = args[0]
                    var reason = "..."

                    if (args.size > 1) {
                        reason = ""
                        for (i in 1 until args.size) {
                            reason += "${args[i]} "
                        }
                    }

                    connection.kick(message.channel, User(nick), reason)
                }
            }
        })
        PluginHelper.instance.addHelp(PluginHelp(
                ".k",
                IRCBotAccess.Level.OPERATOR,
                "Kicks a user from channel",
                "nick",
                "reason"))

        IRCBotAccess.instance.addListener(bot, IRCBotAccess.Level.OPERATOR, object : OnChannelCommandListener {
            override fun channelCommand(): String {
                return ".kb"
            }

            override fun onChannelCommand(connection: Connection, message: ChannelMessage, command: String, args: Array<String>) {
                // FIXME: Copy pasted from .k
                if (args.isNotEmpty()) {
                    val nick = args[0]
                    var reason = "..."

                    if (args.size > 1) {
                        reason = ""
                        for (i in 1 until args.size) {
                            reason += "${args[i]} "
                        }
                    }

                    connection.mode(message.channel, "+b $nick!*@*")
                    connection.kick(message.channel, User(nick), reason)
                }
            }
        })
        PluginHelper.instance.addHelp(PluginHelp(
                ".kb",
                IRCBotAccess.Level.OPERATOR,
                "Kicks and bans a user from channel",
                "nick",
                "reason"))

        IRCBotAccess.instance.addListener(bot, IRCBotAccess.Level.OPERATOR, object : OnChannelCommandListener {
            override fun channelCommand(): String {
                return ".unban"
            }

            override fun onChannelCommand(connection: Connection, message: ChannelMessage, command: String, args: Array<String>) {
                if (args.isNotEmpty()) {
                    val nick = args[0]
                    connection.mode(message.channel, "-b $nick!*@*")
                }
            }
        })
        PluginHelper.instance.addHelp(PluginHelp(
                ".unban",
                IRCBotAccess.Level.OPERATOR,
                "Unbans an user from channel",
                "nick"))

        IRCBotAccess.instance.addListener(bot, IRCBotAccess.Level.OPERATOR, object : OnChannelCommandListener {
            override fun channelCommand(): String {
                return ".mode"
            }

            override fun onChannelCommand(connection: Connection, message: ChannelMessage, command: String, args: Array<String>) {
                connection.mode(message.channel, args.joinToString(" "))
            }
        })
        PluginHelper.instance.addHelp(PluginHelp(
                ".mode",
                IRCBotAccess.Level.OPERATOR,
                "Changes channel modes",
                "mode"))

        IRCBotAccess.instance.addListener(bot, IRCBotAccess.Level.OPERATOR, object : OnChannelCommandListener {
            override fun channelCommand(): String {
                return ".invite"
            }

            override fun onChannelCommand(connection: Connection, message: ChannelMessage, command: String, args: Array<String>) {
                if (args.isNotEmpty()) connection.invite(message.channel, User(args[0]))
            }
        })
        PluginHelper.instance.addHelp(PluginHelp(
                ".mode",
                IRCBotAccess.Level.OPERATOR,
                "Invites an user to channel",
                "nick"))

        IRCBotAccess.instance.addListener(bot, IRCBotAccess.Level.OPERATOR, object : OnChannelCommandListener {
            override fun channelCommand(): String {
                return ".topic"
            }

            override fun onChannelCommand(connection: Connection, message: ChannelMessage, command: String, args: Array<String>) {
                if (args.isNotEmpty()) connection.topic(message.channel, args.joinToString(" "))
            }
        })
        PluginHelper.instance.addHelp(PluginHelp(
                ".topic",
                IRCBotAccess.Level.OPERATOR,
                "Changes a channel topic",
                "topic"))
    }

    override fun onUnload(bot: IRCBot) {
        modifiers.forEach { prefix, _ ->
            modes.forEach { name, _ ->
                bot.removeListener(".$prefix$name")
                PluginHelper.instance.removeHelp(".$prefix$name")
            }
        }

        arrayOf("k", "kb", "unban", "mode", "invite", "topic").forEach {
            bot.removeListener(".$it")
            PluginHelper.instance.removeHelp(".$it")
        }
    }

}