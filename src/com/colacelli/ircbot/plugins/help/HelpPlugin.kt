package com.colacelli.ircbot.plugins.help

import com.colacelli.ircbot.IRCBot
import com.colacelli.ircbot.base.Access
import com.colacelli.ircbot.base.AsciiTable
import com.colacelli.ircbot.base.Help
import com.colacelli.ircbot.base.Plugin
import com.colacelli.ircbot.base.listeners.OnChannelCommandListener
import com.colacelli.irclib.connection.Connection
import com.colacelli.irclib.messages.ChannelMessage
import com.colacelli.irclib.messages.PrivateNoticeMessage

class HelpPlugin : Plugin {
    override var name = "help"

    override fun onLoad(bot: IRCBot) {
        bot.addListener(object : OnChannelCommandListener {
            override val command = ".help"
            override val aliases = arrayOf(".h")
            override val level = Access.Level.USER
            override val help = Help("Show help")

            override fun onChannelCommand(connection: Connection, message: ChannelMessage, command: String, args: Array<String>) {
                val level = bot.access.get(message.sender!!)
                val helps = ArrayList<Array<String>>()
                bot.help.list(level, args.joinToString(" ")).forEach {
                    helps.add(it)
                }
                AsciiTable(arrayOf("Command", "Aliases", "Description"), helps).toText().forEach {
                    connection.send(PrivateNoticeMessage(it, connection.user, message.sender))
                }
            }

        })
    }

    override fun onUnload(bot: IRCBot) {
        bot.removeListener(".help")
    }
}