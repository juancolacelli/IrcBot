package com.colacelli.ircbot.plugins.ircop

import com.colacelli.ircbot.IRCBot
import com.colacelli.ircbot.Plugin
import com.colacelli.ircbot.listeners.OnChannelCommandListener
import com.colacelli.ircbot.plugins.access.IRCBotAccess
import com.colacelli.ircbot.plugins.help.PluginHelp
import com.colacelli.ircbot.plugins.help.PluginHelper
import com.colacelli.irclib.connection.Connection
import com.colacelli.irclib.connection.listeners.OnConnectListener
import com.colacelli.irclib.messages.ChannelMessage

class IRCopPlugin(user : String, password : String) : Plugin {
    val listener = OnConnectListener { connection, _, _ ->
        connection.send("OPER $user $password")
    }

    override fun getName(): String {
        return "ircop"
    }

    override fun onLoad(bot: IRCBot) {
        bot.addListener(listener)

        IRCBotAccess.instance.addListener(bot, IRCBotAccess.Level.ADMIN, object : OnChannelCommandListener {
            override fun channelCommand(): String {
                return ".kill"
            }

            override fun onChannelCommand(connection: Connection, message: ChannelMessage, command: String, args: Array<String>) {
                val user = args[0]

                var reason = "..."
                if (args.size > 1) reason = args.drop(1).joinToString(" ")

                connection.send("KILL $user $reason")
            }

        })

        PluginHelper.instance.addHelp(PluginHelp(
                ".kill",
                IRCBotAccess.Level.ADMIN,
                "Kills a user from server",
                "nick",
                "reason"))
    }

    override fun onUnload(bot: IRCBot) {
        bot.removeListener(listener)
        IRCBotAccess.instance.removeListener(bot, ".kill")
        PluginHelper.instance.removeHelp(".kill")
    }
}