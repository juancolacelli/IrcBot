package com.colacelli.ircbot.plugins.access

import com.colacelli.ircbot.IRCBot
import com.colacelli.ircbot.base.Access
import com.colacelli.ircbot.base.AsciiTable
import com.colacelli.ircbot.base.Help
import com.colacelli.ircbot.base.Plugin
import com.colacelli.ircbot.base.listeners.OnChannelCommandListener
import com.colacelli.irclib.connection.Connection
import com.colacelli.irclib.messages.ChannelMessage
import com.colacelli.irclib.messages.PrivateNoticeMessage

class AccessPlugin : Plugin {
    override var name = "access"

    override fun onLoad(bot: IRCBot) {
        bot.addListener(object : OnChannelCommandListener {
            override val command = ".accessAdd"
            override val aliases = arrayOf(".accAdd", ".acc+")
            override val level = Access.Level.ROOT
            override val help = Help(this,  "Grant user access", "nick", "operator/admin/root")

            override fun onChannelCommand(connection: Connection, message: ChannelMessage, command: String, args: Array<String>) {
                if (args.size == 2) {
                    try {
                        val nick = args[0]
                        val level = Access.Level.valueOf(args[1].toUpperCase())

                        bot.access.add(nick, level)
                        connection.send(PrivateNoticeMessage("Access granted!", connection.user, message.sender))
                    } catch (e: IllegalArgumentException) {
                        connection.send(PrivateNoticeMessage("Invalid access value!", connection.user, message.sender))
                    }
                }
            }
        })

        bot.addListener(object : OnChannelCommandListener {
            override val command = ".accessDel"
            override val aliases = arrayOf(".accDel", ".acc-")
            override val level = Access.Level.ROOT
            override val help = Help(this, "Revoke user access", "nick")

            override fun onChannelCommand(connection: Connection, message: ChannelMessage, command: String, args: Array<String>) {
                if (args.isNotEmpty()) {
                    val nick = args[0]
                    bot.access.del(nick)
                    connection.send(PrivateNoticeMessage("Access revoked!", connection.user, message.sender))
                }
            }
        })

        bot.addListener(object : OnChannelCommandListener {
            override val command = ".accessList"
            override val aliases = arrayOf(".accList", ".acc")
            override val level = Access.Level.OPERATOR
            override val help = Help(this, "List user accesses")

            override fun onChannelCommand(connection: Connection, message: ChannelMessage, command: String, args: Array<String>) {
                var accesses = ArrayList<Array<String>>()
                bot.access.list().forEach {
                    accesses.add(arrayOf(it.key.toString(), it.value.toString()))
                }

                AsciiTable(arrayOf("User", "Level"), accesses).toText().forEach {
                    connection.send(PrivateNoticeMessage(it, connection.user, message.sender))
                }
            }
        })
    }

    override fun onUnload(bot: IRCBot) {
        bot.removeListeners(arrayOf(".accessAdd", ".accessDel", ".accessList"))
    }
}