package com.colacelli.ircbot.plugins.autoresponse

import com.colacelli.ircbot.IRCBot
import com.colacelli.ircbot.base.Access
import com.colacelli.ircbot.base.AsciiTable
import com.colacelli.ircbot.base.Help
import com.colacelli.ircbot.base.Plugin
import com.colacelli.ircbot.base.listeners.OnChannelCommandListener
import com.colacelli.irclib.connection.Connection
import com.colacelli.irclib.connection.listeners.OnChannelMessageListener
import com.colacelli.irclib.messages.ChannelMessage
import com.colacelli.irclib.messages.PrivateNoticeMessage

class AutoResponsePlugin : Plugin {
    companion object {
        const val SEPARATOR = "|"
    }

    val listener = object : OnChannelMessageListener {
        override fun onChannelMessage(connection: Connection, message: ChannelMessage) {
            val text = AutoResponse.instance.get(message)

            if (text != null && text.isNotBlank()) {
                connection.send(ChannelMessage(message.channel, text, connection.user))
            }
        }
    }

    override var name = "auto_response"

    override fun onLoad(bot: IRCBot) {
        bot.addListener(object : OnChannelCommandListener {
            override val command = ".autoResponseAdd"
            override val aliases = arrayOf(".arAdd", ".ar+")
            override val level = Access.Level.ADMIN
            override val help = Help(this, "Adds an auto-response. Available replacements: Regex (\$1, \$2, etc.), \$nick and \$channel", "trigger$SEPARATOR", "response")

            override fun onChannelCommand(connection: Connection, message: ChannelMessage, command: String, args: Array<String>) {
                if (args.isNotEmpty()) {
                    // FIXME: Dirty code...
                    val joinedArgs = args.joinToString(" ").split(SEPARATOR)
                    val trigger = joinedArgs[0]
                    val text = joinedArgs.drop(1).joinToString(SEPARATOR)

                    if (trigger.isNotBlank() && text.isNotBlank()) {
                        AutoResponse.instance.add(trigger, text)
                        connection.send(PrivateNoticeMessage("Auto-response added!", connection.user, message.sender))
                    }
                }
            }
        })

        bot.addListener(object : OnChannelCommandListener {
            override val command = ".autoResponseDel"
            override val aliases = arrayOf(".arDel", ".ar-")
            override val level = Access.Level.ADMIN
            override val help = Help(this, "Removes an auto-response", "trigger")

            override fun onChannelCommand(connection: Connection, message: ChannelMessage, command: String, args: Array<String>) {
                if (args.isNotEmpty()) {
                    val trigger = args.joinToString(" ")
                    AutoResponse.instance.del(trigger)
                    connection.send(PrivateNoticeMessage("Auto-response removed!", connection.user, message.sender))
                }
            }
        })

        bot.addListener(object : OnChannelCommandListener {
            override val command = ".autoResponseList"
            override val aliases = arrayOf(".arList", ".ar")
            override val level = Access.Level.OPERATOR
            override val help = Help(this, "List all auto-responses")

            override fun onChannelCommand(connection: Connection, message: ChannelMessage, command: String, args: Array<String>) {
                var autoResponses = ArrayList<Array<String>>()
                AutoResponse.instance.list().forEach { trigger, text ->
                    autoResponses.add(arrayOf(trigger, text))
                }

                AsciiTable(arrayOf("Trigger", "Response"), autoResponses).toText().forEach {
                    connection.send(PrivateNoticeMessage(it, connection.user, message.sender))
                }
            }
        })

        bot.addListener(listener)
    }

    override fun onUnload(bot: IRCBot) {
        bot.removeListener(listener)
        bot.removeListenersByCommands(arrayOf(".autoResponseAdd", ".autoResponseDel", ".autoResponseList"))
    }
}