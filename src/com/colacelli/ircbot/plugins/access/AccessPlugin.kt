package com.colacelli.ircbot.plugins.access

import com.colacelli.ircbot.IRCBot
import com.colacelli.ircbot.base.Access
import com.colacelli.ircbot.base.Plugin
import com.colacelli.ircbot.base.listeners.OnChannelCommandListener
import com.colacelli.ircbot.base.Help
import com.colacelli.irclib.connection.Connection
import com.colacelli.irclib.messages.ChannelMessage
import com.colacelli.irclib.messages.PrivateNoticeMessage

class AccessPlugin : Plugin {
    override var name = "access"

    override fun onLoad(bot: IRCBot) {
        bot.addListener(object : OnChannelCommandListener {
            override var command = ".accessAdd"
            override var level = Access.Level.ROOT
            override var help = Help("Grant user access", "nick", "value")

            override fun onChannelCommand(connection: Connection, message: ChannelMessage, command: String, args: Array<String>) {
                if (args.size == 2) {
                    try {
                        val nick = args[0]
                        val level = Access.Level.valueOf(args[1].toUpperCase())

                        bot.access.add(nick, level)
                        connection.send(PrivateNoticeMessage("Access granted!", connection.user, message.sender))
                    } catch (e : IllegalArgumentException) {
                        connection.send(PrivateNoticeMessage("Invalid access value!", connection.user, message.sender))
                    }
                }
            }
        })

        bot.addListener(object : OnChannelCommandListener {
            override var command = ".accessDel"
            override var level = Access.Level.ROOT
            override var help = Help("Revoke user access", "nick")

            override fun onChannelCommand(connection: Connection, message: ChannelMessage, command: String, args: Array<String>) {
                if (args.isNotEmpty()) {
                    val nick = args[0]
                    bot.access.del(nick)
                    connection.send(PrivateNoticeMessage("Access revoked!", connection.user, message.sender))
                }
            }
        })

        bot.addListener(object : OnChannelCommandListener {
            override var command = ".accessList"

            override var level = Access.Level.ROOT

            override var help = Help("List user accesses")

            override fun onChannelCommand(connection: Connection, message: ChannelMessage, command: String, args: Array<String>) {
                var accesses = ""
                bot.access.list().forEach {
                    accesses += "${it.key}(${it.value.toString().toLowerCase()}) "
                }

                connection.send(PrivateNoticeMessage(accesses, connection.user, message.sender))
            }
        })
    }

    override fun onUnload(bot: IRCBot) {
        bot.removeListeners(arrayOf(".accessAdd", ".accessDel", ".accessList"))
    }
}