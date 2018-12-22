package com.colacelli.ircbot.plugins.joinpart

import com.colacelli.ircbot.IRCBot
import com.colacelli.ircbot.base.Access
import com.colacelli.ircbot.base.Help
import com.colacelli.ircbot.base.Plugin
import com.colacelli.ircbot.base.listeners.OnChannelCommandListener
import com.colacelli.irclib.actors.Channel
import com.colacelli.irclib.connection.Connection
import com.colacelli.irclib.messages.ChannelMessage

class JoinPartPlugin : Plugin {
    override var name = "join_part"

    override fun onLoad(bot: IRCBot) {
        bot.addListener(object : OnChannelCommandListener {
            override val command = ".join"
            override val aliases: Nothing? = null
            override val level = Access.Level.OPERATOR
            override val help = Help(this, "Joins a channel", "#channel")

            override fun onChannelCommand(connection: Connection, message: ChannelMessage, command: String, args: Array<String>) {
                if (args.isNotEmpty()) {
                    connection.join(Channel(args[0]))
                }
            }

        })

        bot.addListener(object : OnChannelCommandListener {
            override val command = ".part"
            override val aliases: Nothing? = null
            override val level = Access.Level.OPERATOR
            override val help = Help(this, "Parts from a channel", "#channel")

            override fun onChannelCommand(connection: Connection, message: ChannelMessage, command: String, args: Array<String>) {
                if (args.isNotEmpty()) {
                    connection.part(Channel(args[0]))
                }
            }

        })
    }

    override fun onUnload(bot: IRCBot) {
        bot.removeListeners(arrayOf(".join", ".part"))
    }
}