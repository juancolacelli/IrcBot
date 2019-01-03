package com.colacelli.ircbot.plugins.pluginloader

import com.colacelli.ircbot.IRCBot
import com.colacelli.ircbot.base.Access
import com.colacelli.ircbot.base.AsciiTable
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
            override val command = ".pluginLoad"
            override val aliases = arrayOf(".load", ".plug+")
            override val level = Access.Level.ROOT
            override val help = Help(this, "Load a plugin", "plugin")

            override fun onChannelCommand(connection: Connection, message: ChannelMessage, command: String, args: Array<String>) {
                if (args.isNotEmpty()) {
                    val plugin = args[0].toLowerCase()
                    if (bot.pluginLoader.load(plugin)) connection.send(PrivateNoticeMessage("Plugin loaded!", connection.user, message.sender))
                }
            }
        })

        bot.addListener(object : OnChannelCommandListener {
            override val command = ".pluginUnload"
            override val aliases = arrayOf(".unload", ".plug-")
            override val level = Access.Level.ROOT
            override val help = Help(this, "Unload a plugin", "plugin")

            override fun onChannelCommand(connection: Connection, message: ChannelMessage, command: String, args: Array<String>) {
                if (args.isNotEmpty()) {
                    val plugin = args[0].toLowerCase()
                    if (bot.pluginLoader.unload(plugin)) connection.send(PrivateNoticeMessage("Plugin unloaded!", connection.user, message.sender))
                }
            }
        })

        bot.addListener(object : OnChannelCommandListener {
            override val command = ".pluginList"
            override val aliases = arrayOf(".plugList", ".plug")
            override val level = Access.Level.ROOT
            override val help = Help(this, "List all available plugins")

            override fun onChannelCommand(connection: Connection, message: ChannelMessage, command: String, args: Array<String>) {
                var plugins = ArrayList<Array<String>>()
                bot.pluginLoader.list().forEach {
                    plugins.add(arrayOf(it))
                }

                AsciiTable(arrayOf("Plugin"), plugins).toText().forEach {
                    connection.send(PrivateNoticeMessage(it, connection.user, message.sender))
                }
            }
        })
    }

    override fun onUnload(bot: IRCBot) {
        bot.removeListenersByCommands(arrayOf(".pluginLoad", ".pluginUnload", ".pluginList"))
    }
}