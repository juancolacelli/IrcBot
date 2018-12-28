package com.colacelli.ircbot.plugins.ircop

import com.colacelli.ircbot.IRCBot
import com.colacelli.ircbot.base.Access
import com.colacelli.ircbot.base.Help
import com.colacelli.ircbot.base.Plugin
import com.colacelli.ircbot.base.listeners.OnChannelCommandListener
import com.colacelli.irclib.actors.User
import com.colacelli.irclib.connection.Connection
import com.colacelli.irclib.connection.Server
import com.colacelli.irclib.connection.listeners.OnConnectListener
import com.colacelli.irclib.messages.ChannelMessage

class IRCopPlugin(name: String, password: String) : Plugin {
    val listener = object : OnConnectListener {
        override fun onConnect(connection: Connection, server: Server, user: User) {
            return connection.send("OPER $name $password")
        }
    }

    override var name = "ircop"

    override fun onLoad(bot: IRCBot) {
        bot.addListener(listener)

        bot.addListener(object : OnChannelCommandListener {
            override val command = ".kill"
            override val aliases: Nothing? = null
            override val level = Access.Level.ADMIN
            override val help = Help(this, "Kills a user from server", "nick", "reason")

            override fun onChannelCommand(connection: Connection, message: ChannelMessage, command: String, args: Array<String>) {
                val user = args[0]

                if (user.toLowerCase() != connection.user.nick.toLowerCase()) {
                    var reason = "..."
                    if (args.size > 1) reason = args.drop(1).joinToString(" ")

                    connection.kill(User(user), reason)
                }
            }

        })
    }

    override fun onUnload(bot: IRCBot) {
        bot.removeListener(listener)
        bot.removeListener(".kill")
    }
}