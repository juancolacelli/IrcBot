package com.colacelli.ircbot.plugins.help

import com.colacelli.ircbot.IRCBot
import com.colacelli.ircbot.Plugin
import com.colacelli.ircbot.listeners.OnChannelCommandListener
import com.colacelli.ircbot.plugins.access.IRCBotAccess
import com.colacelli.irclib.connection.Connection
import com.colacelli.irclib.messages.ChannelMessage
import com.colacelli.irclib.messages.PrivateNoticeMessage

class HelpPlugin : Plugin {
    override fun getName(): String {
        return "help"
    }

    override fun onLoad(bot: IRCBot) {
        bot.addListener(listener)
    }

    override fun onUnload(bot: IRCBot) {
        bot.removeListener(".help")
    }

    val listener = object : OnChannelCommandListener {
        override fun channelCommand(): String {
            return ".help"
        }

        override fun onChannelCommand(connection: Connection, message: ChannelMessage, command: String, args: Array<String>) {
            val access = IRCBotAccess.instance.getAccess(message.sender)

            if (args.isEmpty()) {
                var text = "Available commands: (use .help <command> for more information)"

                val commands = PluginHelper.instance.getCommands(access)
                commands.sort()

                commands.forEach {
                    if (text.indexOf(command) == -1) text += " ${it.substring(0, it.indexOf(" "))}"
                }

                val response = PrivateNoticeMessage.Builder()
                        .setSender(connection.user)
                        .setReceiver(message.sender)
                        .setText(text)
                        .build()

                connection.send(response)
            } else {
                PluginHelper.instance.getHelp(access, args.joinToString(" ")).forEach {
                    val response = PrivateNoticeMessage.Builder()
                            .setSender(connection.user)
                            .setReceiver(message.sender)
                            .setText(it)
                            .build()

                    connection.send(response)
                }
            }
        }

    }
}