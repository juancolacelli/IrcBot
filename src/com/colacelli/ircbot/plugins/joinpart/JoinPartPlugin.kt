package com.colacelli.ircbot.plugins.joinpart

import com.colacelli.ircbot.IRCBot
import com.colacelli.ircbot.Plugin
import com.colacelli.ircbot.listeners.OnChannelCommandListener
import com.colacelli.ircbot.plugins.access.IRCBotAccess
import com.colacelli.ircbot.plugins.help.PluginHelp
import com.colacelli.ircbot.plugins.help.PluginHelper
import com.colacelli.irclib.actors.Channel
import com.colacelli.irclib.connection.Connection
import com.colacelli.irclib.messages.ChannelMessage

class JoinPartPlugin : Plugin {
    override fun getName(): String {
        return "join_part"
    }

    override fun onLoad(bot: IRCBot) {
        IRCBotAccess.instance.addListener(bot, IRCBotAccess.Level.OPERATOR, object : OnChannelCommandListener {
            override fun channelCommand(): String {
                return ".join"
            }

            override fun onChannelCommand(connection: Connection, message: ChannelMessage, command: String, args: Array<String>) {
                val channel = Channel(args[0])
                connection.join(channel)
            }

        })

        PluginHelper.instance.addHelp(PluginHelp(
                ".join",
                IRCBotAccess.Level.OPERATOR,
                "Joins a channel",
                "#channel"))

        IRCBotAccess.instance.addListener(bot, IRCBotAccess.Level.OPERATOR, object : OnChannelCommandListener {
            override fun channelCommand(): String {
                return ".part"
            }

            override fun onChannelCommand(connection: Connection, message: ChannelMessage, command: String, args: Array<String>) {
                val channel = Channel(args[0])
                connection.part(channel)
            }

        })

        PluginHelper.instance.addHelp(PluginHelp(
                ".part",
                IRCBotAccess.Level.OPERATOR,
                "Joins a channel",
                "#channel"))
    }

    override fun onUnload(bot: IRCBot) {
        arrayOf(".join", ".part").forEach {
            IRCBotAccess.instance.removeListener(bot, it)
            PluginHelper.instance.removeHelp(it)
        }
    }
}