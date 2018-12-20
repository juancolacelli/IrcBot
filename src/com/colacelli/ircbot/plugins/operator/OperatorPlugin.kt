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
            "+q" to arrayOf(".owner"),
            "+a" to arrayOf(".protect"),
            "+o" to arrayOf(".op"),
            "+h" to arrayOf(".halfOp", ".h"),
            "+v" to arrayOf(".voice", ".v"),
            "-q" to arrayOf(".deOwner"),
            "-a" to arrayOf(".deProtect"),
            "-o" to arrayOf(".deOp"),
            "-h" to arrayOf(".deHalfOp", ".deH"),
            "-v" to arrayOf(".deVoice", ".deV")
    )

    override var name = "operator"

    override fun onLoad(bot: IRCBot) {
        modes.forEach { mode, commands ->
            val command = commands[0]
            commands.drop(0)
            bot.addListener(object : OnChannelCommandListener {
                override val command = command
                override val aliases = commands
                override val level = Access.Level.OPERATOR
                override val help = Help(this, "$mode user channel mode", "nick1", "nick2")

                override fun onChannelCommand(connection: Connection, message: ChannelMessage, command: String, args: Array<String>) {
                    if (args.isNotEmpty()) {
                        var nicks = ""
                        var modes = ""
                        args.forEach {
                            if (it.toLowerCase() != connection.user.nick.toLowerCase()) {
                                modes += mode
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
            override val command = ".kick"
            override val aliases = arrayOf(".k")
            override val level = Access.Level.OPERATOR
            override val help = Help(this, "Kicks a user from channel", "nick", "reason")

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
            override val command = ".kickBan"
            override val aliases = arrayOf(".kb")
            override val level = Access.Level.OPERATOR
            override val help = Help(this, "Kick and bans a user from channel", "nick", "reason")

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
            override val command = ".unBan"
            override val aliases: Nothing? = null
            override val level = Access.Level.OPERATOR
            override val help = Help(this, "Remove user bans from channel", "nick")

            override fun onChannelCommand(connection: Connection, message: ChannelMessage, command: String, args: Array<String>) {
                if (args.isNotEmpty()) {
                    val nick = args[0]
                    connection.mode(message.channel, "-b $nick!*@*")
                }
            }
        })

        bot.addListener(object : OnChannelCommandListener {
            override val command = ".mode"
            override val aliases: Nothing? = null
            override val level = Access.Level.OPERATOR
            override val help = Help(this, "Change channel modes", "modes")

            override fun onChannelCommand(connection: Connection, message: ChannelMessage, command: String, args: Array<String>) {
                connection.mode(message.channel, args.joinToString(" "))
            }
        })

        bot.addListener(object : OnChannelCommandListener {
            override val command = ".invite"
            override val aliases: Nothing? = null
            override val level = Access.Level.OPERATOR
            override val help = Help(this, "Invite an user to channel", "nick")

            override fun onChannelCommand(connection: Connection, message: ChannelMessage, command: String, args: Array<String>) {
                if (args.isNotEmpty()) connection.invite(message.channel, User(args[0]))
            }
        })

        bot.addListener(object : OnChannelCommandListener {
            override val command = ".topic"
            override val aliases: Nothing? = null
            override val level = Access.Level.OPERATOR
            override val help = Help(this, "Change channel topic", "topic")

            override fun onChannelCommand(connection: Connection, message: ChannelMessage, command: String, args: Array<String>) {
                if (args.isNotEmpty()) connection.topic(message.channel, args.joinToString(" "))
            }
        })
    }

    override fun onUnload(bot: IRCBot) {
        modes.forEach { _, commands ->
            bot.removeListener(commands[0])
        }
        bot.removeListeners(arrayOf(".kick", ".kickBan", ".unBan", ".mode", ".invite", ".topic"))
    }

}