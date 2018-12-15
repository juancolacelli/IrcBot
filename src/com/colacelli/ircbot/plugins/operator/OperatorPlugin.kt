package com.colacelli.ircbot.plugins.operator

import com.colacelli.ircbot.IRCBot
import com.colacelli.ircbot.base.Access
import com.colacelli.ircbot.base.Help
import com.colacelli.ircbot.base.Plugin
import com.colacelli.ircbot.base.listeners.OnChannelCommandListener
import com.colacelli.irclib.actors.User
import com.colacelli.irclib.connection.Connection
import com.colacelli.irclib.messages.ChannelMessage

class OperatorPlugin : Plugin {
    private val modes = hashMapOf(
            "owner" to "+q",
            "protect" to "+a",
            "op" to "+o",
            "halfOp" to "+h",
            "voice" to "+v",
            "deOwner" to "-q",
            "deProtect" to "-a",
            "deOp" to "-o",
            "deHalfOp" to "-h",
            "deVoice" to "-v"
    )

    override var name = "operator"

    override fun onLoad(bot: IRCBot) {
        modes.forEach { name, mode ->
            bot.addListener(object : OnChannelCommandListener {
                override var command = ".$name"
                override var level = Access.Level.OPERATOR
                override var help = Help("$mode user channel mode", "nick1", "nick2")

                override fun onChannelCommand(connection: Connection, message: ChannelMessage, command: String, args: Array<String>) {
                    if (args.isNotEmpty()) {
                        var nicks = ""
                        var modes = ""
                        args.forEach {
                            if (it.toLowerCase() != connection.user.nick.toLowerCase()) {
                                modes += "$mode"
                                nicks += "$it "
                            }
                        }

                        connection.mode(message.channel, "$modes $nicks")
                    } else {
                        connection.mode(message.channel, "$mode ${message.sender?.nick}")
                    }
                }
            })
        }

        bot.addListener(object : OnChannelCommandListener {
            override var command = ".kick"
            override var level = Access.Level.OPERATOR
            override var help = Help("Kicks a user from channel", "nick", "reason")

            override fun onChannelCommand(connection: Connection, message: ChannelMessage, command: String, args: Array<String>) {
                if (args.isNotEmpty()) {
                    val nick = args[0]
                    var reason = "..."

                    if (args.size > 1) {
                        reason = args.drop(1).joinToString(" ")
                    }

                    connection.kick(message.channel, User(nick), reason)
                }
            }
        })

        bot.addListener(object : OnChannelCommandListener {
            override var command = ".kickBan"
            override var level = Access.Level.OPERATOR
            override var help = Help("Kick and bans a user from channel", "nick", "reason")

            override fun onChannelCommand(connection: Connection, message: ChannelMessage, command: String, args: Array<String>) {
                // FIXME: Copy pasted from .kick
                if (args.isNotEmpty()) {
                    val nick = args[0]
                    var reason = "..."

                    if (args.size > 1) {
                        reason = args.drop(1).joinToString(" ")
                    }

                    connection.mode(message.channel, "+b $nick!*@*")
                    connection.kick(message.channel, User(nick), reason)
                }
            }
        })

        bot.addListener(object : OnChannelCommandListener {
            override var command = ".unBan"
            override var level = Access.Level.OPERATOR
            override var help = Help("Remove user bans from channel", "nick")

            override fun onChannelCommand(connection: Connection, message: ChannelMessage, command: String, args: Array<String>) {
                if (args.isNotEmpty()) {
                    val nick = args[0]
                    connection.mode(message.channel, "-b $nick!*@*")
                }
            }
        })

        bot.addListener(object : OnChannelCommandListener {
            override var command = ".mode"
            override var level = Access.Level.OPERATOR
            override var help = Help("Change channel modes", "modes")

            override fun onChannelCommand(connection: Connection, message: ChannelMessage, command: String, args: Array<String>) {
                connection.mode(message.channel, args.joinToString(" "))
            }
        })

        bot.addListener(object : OnChannelCommandListener {
            override var command = ".invite"
            override var level = Access.Level.OPERATOR
            override var help = Help("Invite an user to channel", "nick")

            override fun onChannelCommand(connection: Connection, message: ChannelMessage, command: String, args: Array<String>) {
                if (args.isNotEmpty()) connection.invite(message.channel, User(args[0]))
            }
        })

        bot.addListener(object : OnChannelCommandListener {
            override var command = ".topic"
            override var level = Access.Level.OPERATOR
            override var help = Help("Change channel topic", "topic")

            override fun onChannelCommand(connection: Connection, message: ChannelMessage, command: String, args: Array<String>) {
                if (args.isNotEmpty()) connection.topic(message.channel, args.joinToString(" "))
            }
        })
    }

    override fun onUnload(bot: IRCBot) {
        bot.removeListeners(modes.values.toTypedArray())
        bot.removeListeners(arrayOf(".kick", ".kickBan", ".unBan", ".mode", ".invite", ".topic"))
    }

}