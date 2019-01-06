package com.colacelli.ircbot.plugins.joinpart

import com.colacelli.ircbot.IRCBot
import com.colacelli.ircbot.base.Access
import com.colacelli.ircbot.base.Help
import com.colacelli.ircbot.base.Plugin
import com.colacelli.ircbot.base.listeners.OnChannelCommandListener
import com.colacelli.irclib.actors.Channel
import com.colacelli.irclib.actors.User
import com.colacelli.irclib.connection.Connection
import com.colacelli.irclib.connection.Server
import com.colacelli.irclib.connection.listeners.OnConnectListener
import com.colacelli.irclib.messages.ChannelMessage

class JoinPartPlugin : Plugin {
    val manager = ChannelsManager()

    override var name = "join_part"

    private var listener = object : OnConnectListener {
        override fun onConnect(connection: Connection, server: Server, user: User) {
            return manager.list().forEach {
                connection.join(Channel(it))
            }
        }
    }

    override fun onLoad(bot: IRCBot) {
        bot.addListener(listener)
        bot.addListener(object : OnChannelCommandListener {
            override val command = ".join"
            override val aliases: Nothing? = null
            override val level = Access.Level.OPERATOR
            override val help = Help(this, "Joins a channel", "#channel")

            override fun onChannelCommand(connection: Connection, message: ChannelMessage, command: String, args: Array<String>) {
                if (args.isNotEmpty()) {
                    manager.add(args[0])
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
                    manager.del(args[0])
                    connection.part(Channel(args[0]))
                }
            }

        })
    }

    override fun onUnload(bot: IRCBot) {
        bot.addListener(listener)
        bot.removeListenersByCommands(arrayOf(".join", ".part"))
    }
}