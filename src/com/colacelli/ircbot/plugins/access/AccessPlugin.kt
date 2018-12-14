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
            override val commands: Array<String>
                get() = arrayOf(".access", ".acc")

            override fun onChannelCommand(connection: Connection, message: ChannelMessage, command: String, args: Array<String>) {
                if (args.size > 1) {
                    when (args[0]) {
                        "add" -> {
                            if (args.size == 3) {
                                try {
                                    val level = IRCBotAccess.Level.valueOf(args[2].toUpperCase())
                                    val nick = args[1]

                                    IRCBotAccess.instance.add(nick, level)
                                    connection.send(PrivateNoticeMessage("Access granted!", connection.user, message.sender))
                                } catch (e : IllegalArgumentException) {
                                    connection.send(PrivateNoticeMessage("Invalid access level!", connection.user, message.sender))
                                }
                            }
                        }

                        "del" -> {
                            val nick = args[1]
                            IRCBotAccess.instance.del(nick)
                            connection.send(PrivateNoticeMessage("Access revoked!", connection.user, message.sender))
                        }
                    }
                } else {
                    when (args[0]) {
                        "list" -> {
                            var accesses = ""
                            IRCBotAccess.instance.list().forEach {
                                accesses += "${it.key}(${it.value.toString().toLowerCase()}) "
                            }

                            connection.send(PrivateNoticeMessage(accesses, connection.user, message.sender))
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