package com.colacelli.ircbot.plugins.autoresponse

import com.colacelli.ircbot.IRCBot
import com.colacelli.ircbot.Plugin
import com.colacelli.ircbot.listeners.OnChannelCommandListener
import com.colacelli.ircbot.plugins.access.IRCBotAccess
import com.colacelli.ircbot.plugins.help.PluginHelp
import com.colacelli.ircbot.plugins.help.PluginHelper
import com.colacelli.irclib.connection.Connection
import com.colacelli.irclib.connection.listeners.OnChannelMessageListener
import com.colacelli.irclib.messages.ChannelMessage
import com.colacelli.irclib.messages.PrivateNoticeMessage

class AutoResponsePlugin :Plugin {
    val listener = OnChannelMessageListener { connection, message ->
        val text = AutoResponse.instance.get(message)

        when (text?.isNotBlank()) {
            true -> {
                val response = ChannelMessage.Builder()
                        .setSender(connection.user)
                        .setChannel(message.channel)
                        .setText(text)
                        .build()

                connection.send(response)
            }
        }
    }

    override fun getName(): String {
        return "auto_response"
    }

    override fun onLoad(bot: IRCBot) {
        IRCBotAccess.instance.addListener(bot, IRCBotAccess.Level.ADMIN, object : OnChannelCommandListener {
            override fun channelCommand(): String {
                return ".ar"
            }

            override fun onChannelCommand(connection: Connection, message: ChannelMessage, command: String, args: Array<String>) {
                val response = PrivateNoticeMessage.Builder()
                        .setSender(connection.user)
                        .setReceiver(message.sender)

                if (args.size > 1) {
                    when (args[0]) {
                        "add" -> {
                            // FIXME: Dirty code...
                            val joinedArgs = args.drop(1).joinToString(" ")
                            val separatorIndex = joinedArgs.indexOf(AutoResponsePluginHelp.SEPARATOR)
                            val trigger = joinedArgs.substring(0, separatorIndex)
                            val text = joinedArgs.substring(separatorIndex + 1)
                            AutoResponse.instance.add(trigger, text)

                            response.setText("Autoresponse added!")
                            connection.send(response.build())
                        }

                        "del" -> {
                            val trigger = args[1]
                            AutoResponse.instance.del(trigger)

                            response.setText("Autoresponse removed!")
                            connection.send(response.build())
                        }
                    }
                } else {
                    when (args[0]) {
                        "list" -> {
                            AutoResponse.instance.list().forEach { trigger, text ->
                                response.setText("$trigger: $text")
                                connection.send(response.build())
                            }
                        }
                    }
                }
            }
        })

        PluginHelper.instance.addHelp(AutoResponsePluginHelp(
                ".autoresponse add",
                IRCBotAccess.Level.OPERATOR,
                "Adds an autoresponse. Available replacements: \$nick and \$channel, ie., .autoresponse add hello" + AutoResponsePluginHelp.SEPARATOR + "hello \$nick, welcome to \$channel!",
                "trigger",
                "response"))

        PluginHelper.instance.addHelp(PluginHelp(
                ".autoresponse del",
                IRCBotAccess.Level.OPERATOR,
                "Removes an autoresponse",
                "trigger"))

        PluginHelper.instance.addHelp(PluginHelp(
                ".autoresponse list",
                IRCBotAccess.Level.OPERATOR,
                "List all autoresponses"))

        bot.addListener(listener)
    }

    override fun onUnload(bot: IRCBot) {
        bot.removeListener(".autoresponse")
        bot.removeListener(listener)
        arrayOf("add", "del", "list").forEach {
            PluginHelper.instance.removeHelp(".autoresponse $it")
        }
    }
}