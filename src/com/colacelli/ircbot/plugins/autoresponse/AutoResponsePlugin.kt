package com.colacelli.ircbot.plugins.autoresponse

import com.colacelli.ircbot.IRCBot
import com.colacelli.ircbot.base.Plugin
import com.colacelli.ircbot.base.listeners.OnChannelCommandListener
import com.colacelli.ircbot.base.Access
import com.colacelli.ircbot.base.Help
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
            override var command = ".autoResponseAdd"
            override var level = Access.Level.ADMIN
            override var help = Help("Adds an auto-response. Available replacements: Regex (\$1, \$2, etc.), \$nick and \$channel", "trigger$SEPARATOR", "response")

            override fun onChannelCommand(connection: Connection, message: ChannelMessage, command: String, args: Array<String>) {
                if (args.size > 1) {
                    // FIXME: Dirty code...
                    val joinedArgs = args.joinToString(" ").split("")
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
            override var command = ".autoResponseDel"
            override var level = Access.Level.ADMIN
            override var help = Help("Removes an auto-response", "trigger")

            override fun onChannelCommand(connection: Connection, message: ChannelMessage, command: String, args: Array<String>) {
                if (args.size > 1) {
                    val trigger = args.joinToString(" ")
                    AutoResponse.instance.del(trigger)
                    connection.send(PrivateNoticeMessage("Auto-response removed!", connection.user, message.sender))
                }
            }
        })

        bot.addListener(object : OnChannelCommandListener {
            override var command = ".autoResponseList"
            override var level = Access.Level.ADMIN
            override var help = Help("List all auto-responses")

            override fun onChannelCommand(connection: Connection, message: ChannelMessage, command: String, args: Array<String>) {
                AutoResponse.instance.list().forEach { trigger, text ->
                    connection.send(PrivateNoticeMessage("$trigger: $text", connection.user, message.sender))
                }
            }
        })

        bot.addListener(listener)
    }

    override fun onUnload(bot: IRCBot) {
        bot.removeListener(listener)
        bot.removeListeners(arrayOf(".autoResponseAdd", ".autoResponseDel", ".autoResponseList"))
    }
}