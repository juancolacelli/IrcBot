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
            val access = IRCBotAccess.instance.get(message.sender!!)

            if (args.isEmpty() || args[0].isBlank()) {
                var text = "Available commands: (use .help <command> for more information)"

                PluginHelper.instance.getCommands(access).forEach {
                    text += " $it"
                }

                connection.send(PrivateNoticeMessage(text, connection.user, message.sender))
            } else {
                PluginHelper.instance.getHelp(access, args.joinToString(" ")).forEach {
                    connection.send(PrivateNoticeMessage(it, connection.user, message.sender))
                }
            }
        }

    }
}