package com.colacelli.ircbot.plugins.pluginloader

import com.colacelli.ircbot.IRCBot
import com.colacelli.ircbot.base.Access
import com.colacelli.ircbot.base.Help
import com.colacelli.ircbot.base.Plugin
import com.colacelli.ircbot.base.listeners.OnChannelCommandListener
import com.colacelli.irclib.connection.Connection
import com.colacelli.irclib.messages.ChannelMessage
import com.colacelli.irclib.messages.PrivateNoticeMessage

class PluginLoaderPlugin : Plugin {
    override var name = "plugin_loader"

    override fun onLoad(bot: IRCBot) {
        bot.addListener(object : OnChannelCommandListener {
            override var command = ".pluginLoad"
            override var level = Access.Level.ROOT
            override var help = Help("Load a plugin", "plugin")

            override fun onChannelCommand(connection: Connection, message: ChannelMessage, command: String, args: Array<String>) {
                if (args.isNotEmpty()) {
                    val plugin = args[0].toLowerCase()
                    bot.plugins.forEach {
                        if (it.name.toLowerCase() == plugin) {
                            it.onLoad(bot)
                            connection.send(PrivateNoticeMessage("Plugin loaded!", connection.user, message.sender))
                        }
                    }
                }
            }
        })

        bot.addListener(object : OnChannelCommandListener {
            override var command = ".pluginUnload"
            override var level = Access.Level.ROOT
            override var help = Help("Unload a plugin", "plugin")

            override fun onChannelCommand(connection: Connection, message: ChannelMessage, command: String, args: Array<String>) {
                if (args.isNotEmpty()) {
                    val plugin = args[0].toLowerCase()
                    bot.plugins.forEach {
                        if (it.name.toLowerCase() == plugin) {
                            it.onUnload(bot)
                            connection.send(PrivateNoticeMessage("Plugin unloaded!", connection.user, message.sender))
                        }
                    }
                }
            }
        })

        bot.addListener(object : OnChannelCommandListener {
            override var command = ".pluginList"
            override var level = Access.Level.ROOT
            override var help = Help("List all available plugins")

            override fun onChannelCommand(connection: Connection, message: ChannelMessage, command: String, args: Array<String>) {
                var plugins = ""
                bot.plugins.forEach {
                    plugins += "${it.name} "
                }
                connection.send(PrivateNoticeMessage(plugins, connection.user, message.sender))
            }
        })
    }

    override fun onUnload(bot: IRCBot) {
        bot.removeListeners(arrayOf(".pluginList", ".pluginLoad", ".pluginUnload"))
    }
}