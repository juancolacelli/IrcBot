package com.colacelli.ircbot.plugins.help

import com.colacelli.ircbot.IRCBot
import com.colacelli.ircbot.base.Plugin
import com.colacelli.ircbot.base.listeners.OnChannelCommandListener
import com.colacelli.ircbot.base.Access
import com.colacelli.ircbot.base.Help
import com.colacelli.irclib.connection.Connection
import com.colacelli.irclib.messages.ChannelMessage
import com.colacelli.irclib.messages.PrivateNoticeMessage

class HelpPlugin : Plugin {
    override var name = "help"

    override fun onLoad(bot: IRCBot) {
        bot.addListener(object : OnChannelCommandListener {
            override var command = ".help"
            override var level = Access.Level.USER
            override var help = Help("Show help")

            override fun onChannelCommand(connection: Connection, message: ChannelMessage, command: String, args: Array<String>) {
                val level = bot.access.get(message.sender!!)

                if (args.isEmpty() || args[0].isBlank()) {
                    var text = "Available commands:"

                    bot.help.list(level).forEach {
                        text += " $it"
                    }

                    connection.send(PrivateNoticeMessage(text, connection.user, message.sender))
                } else {
                    bot.help.get(level, args.joinToString(" ")).forEach {
                        connection.send(PrivateNoticeMessage(it, connection.user, message.sender))
                    }
                }
            }

        })
    }

    override fun onUnload(bot: IRCBot) {
        bot.removeListener(".help")
    }
}