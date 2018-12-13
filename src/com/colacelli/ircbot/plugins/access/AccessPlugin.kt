package com.colacelli.ircbot.plugins.access

import com.colacelli.ircbot.IRCBot
import com.colacelli.ircbot.Plugin
import com.colacelli.ircbot.listeners.OnChannelCommandListener
import com.colacelli.ircbot.plugins.help.PluginHelp
import com.colacelli.ircbot.plugins.help.PluginHelper
import com.colacelli.irclib.connection.Connection
import com.colacelli.irclib.messages.ChannelMessage
import com.colacelli.irclib.messages.PrivateNoticeMessage

class AccessPlugin : Plugin {
    override fun getName(): String {
        return "access"
    }

    override fun onLoad(bot: IRCBot) {
        IRCBotAccess.instance.addListener(bot, IRCBotAccess.Level.ROOT, object : OnChannelCommandListener {
            override fun channelCommand(): String {
                return ".access"
            }

            override fun onChannelCommand(connection: Connection, message: ChannelMessage, command: String, args: Array<String>) {
                val response = PrivateNoticeMessage.Builder()
                        .setSender(connection.user)
                        .setReceiver(message.sender)

                if (args.size > 1) {
                    when (args[0]) {
                        "add" -> {
                            try {
                                val level = IRCBotAccess.Level.valueOf(args[2].toUpperCase())
                                val nick = args[1]

                                IRCBotAccess.instance.add(nick, level)

                                response.setText("Access granted!")
                                connection.send(response.build())
                            } catch (e : IllegalArgumentException) {
                                response.setText("Invalid access level!")
                                connection.send(response.build())
                            }
                        }

                        "del" -> {
                            val nick = args[1]
                            IRCBotAccess.instance.del(nick)

                            response.setText("Access revoked!")
                            connection.send(response.build())
                        }
                    }
                } else {
                    when (args[0]) {
                        "list" -> {
                            var accesses = ""
                            IRCBotAccess.instance.list().forEach {
                                accesses += "${it.key}(${it.value.toString().toLowerCase()}) "
                            }

                            response.setText(accesses)
                            connection.send(response.build())
                        }
                    }
                }
            }
        })

        PluginHelper.instance.addHelp(PluginHelp(
                ".access add",
                IRCBotAccess.Level.ROOT,
                "Grant user access",
                "user",
                "root/admin/operator"))

        PluginHelper.instance.addHelp(PluginHelp(
                ".access del",
                IRCBotAccess.Level.ROOT,
                "Revoke user access",
                "user"))

        PluginHelper.instance.addHelp(PluginHelp(
                ".access list",
                IRCBotAccess.Level.ROOT,
                "Show access list"))
    }

    override fun onUnload(bot: IRCBot) {
        bot.removeListener(".access")
        arrayOf("add", "del", "list").forEach {
            PluginHelper.instance.removeHelp(".access $it")
        }
    }
}