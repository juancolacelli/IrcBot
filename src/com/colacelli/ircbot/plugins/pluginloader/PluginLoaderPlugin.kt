package com.colacelli.ircbot.plugins.pluginloader

import com.colacelli.ircbot.IRCBot
import com.colacelli.ircbot.Plugin
import com.colacelli.ircbot.listeners.OnChannelCommandListener
import com.colacelli.ircbot.plugins.access.IRCBotAccess
import com.colacelli.ircbot.plugins.help.PluginHelp
import com.colacelli.ircbot.plugins.help.PluginHelper
import com.colacelli.irclib.connection.Connection
import com.colacelli.irclib.messages.ChannelMessage
import com.colacelli.irclib.messages.PrivateNoticeMessage

class PluginLoaderPlugin : Plugin {
    override fun getName(): String {
        return "plugin_loader"
    }

    override fun onLoad(bot: IRCBot) {
        IRCBotAccess.instance.addListener(bot, IRCBotAccess.Level.ROOT, object : OnChannelCommandListener {
            override fun channelCommand(): String {
                return ".plugin"
            }

            override fun onChannelCommand(connection: Connection, message: ChannelMessage, command: String, args: Array<String>) {
                if (args.size > 1) {
                    when (args[0]) {
                        "load" -> {
                            val plugin = args[1].toLowerCase()
                            bot.plugins.forEach {
                                if (it.getName().toLowerCase() == plugin) {
                                    it.onLoad(bot)
                                    connection.send(PrivateNoticeMessage("Plugin loaded!", connection.user, message.sender))
                                }
                            }
                        }

                        "unload" -> {
                            val plugin = args[1].toLowerCase()
                            bot.plugins.forEach {
                                if (it.getName().toLowerCase() == plugin) {
                                    it.onUnload(bot)
                                    connection.send(PrivateNoticeMessage("Plugin unloaded!", connection.user, message.sender))
                                }
                            }
                        }
                    }
                } else {
                    when (args[0]) {
                        "list" -> {
                            bot.plugins.forEach {
                                connection.send(PrivateNoticeMessage(it.getName(), connection.user, message.sender))
                            }
                        }
                    }
                }
            }
        })

        PluginHelper.instance.addHelp(PluginHelp(
                ".plugin list",
                IRCBotAccess.Level.ROOT,
                "List all available plugins"))

        PluginHelper.instance.addHelp(PluginHelp(
                ".plugin load",
                IRCBotAccess.Level.ROOT,
                "Load a plugin",
                "plugin"))

        PluginHelper.instance.addHelp(PluginHelp(
                ".plugin unload",
                IRCBotAccess.Level.ROOT,
                "Unload a plugin",
                "plugin"))
    }

    override fun onUnload(bot: IRCBot) {
        bot.removeListener(".plugin")
        arrayOf("list", "load", "unload").forEach {
            PluginHelper.instance.removeHelp(".plugin $it")
        }
    }
}