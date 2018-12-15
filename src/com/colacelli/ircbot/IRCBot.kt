package com.colacelli.ircbot

import com.colacelli.ircbot.base.Access
import com.colacelli.ircbot.base.Helper
import com.colacelli.ircbot.base.Plugin
import com.colacelli.ircbot.base.listeners.OnAccessCheckListener
import com.colacelli.ircbot.base.listeners.OnChannelCommandListener
import com.colacelli.irclib.actors.User
import com.colacelli.irclib.connection.Connection
import com.colacelli.irclib.connection.Server
import com.colacelli.irclib.connection.listeners.Listenable
import com.colacelli.irclib.connection.listeners.Listener
import com.colacelli.irclib.connection.listeners.OnChannelMessageListener
import com.colacelli.irclib.messages.ChannelMessage
import kotlin.collections.ArrayList

class IRCBot(val server: Server, val user: User) : Listenable {
    val connection = Connection(server, user)

    val access = Access(this)
    val help = Helper(this)
    val listeners = ArrayList<OnChannelCommandListener>()
    val plugins = ArrayList<Plugin>()

    companion object {
        const val HTTP_USER_AGENT = "GNU IRC Bot - https://gitlab.com/jic/ircbot"
    }

    init {
        System.setProperty("http.agent", HTTP_USER_AGENT)
        addListener(object : OnChannelMessageListener {
            override fun onChannelMessage(connection: Connection, message: ChannelMessage) {
                val words = message.text.split(" ")
                val command = words[0].toLowerCase()

                // Remove the command
                val args = words.drop(1).toTypedArray()

                listeners.forEach {
                    if (it.command.toLowerCase() == command) {
                        access.check(message.sender!!, it.level, object : OnAccessCheckListener {
                            override fun onSuccess(user: User, level: Access.Level) {
                                it.onChannelCommand(connection, message, command, args)
                            }

                            override fun onError(user: User, level: Access.Level) {
                            }
                        })
                    }
                }
            }
        })
    }

    override fun addListener(listener: Listener) {
        connection.addListener(listener)
    }

    override fun removeListener(listener: Listener) {
        connection.removeListener(listener)
    }

    fun addListener(listener: OnChannelCommandListener) {
        listener.help.command = listener.command

        listeners.add(listener)
    }

    fun removeListener(command: String) {
        var toRemove = ArrayList<OnChannelCommandListener>()

        listeners.forEach {
            if (it.command.toLowerCase() == command) toRemove.add(it)
        }

        listeners.removeAll(toRemove)
    }

    fun removeListeners(commands: Array<String>) {
        commands.forEach {
            removeListener(it)
        }
    }

    fun connect() {
        connection.connect()
    }

    fun addPlugin(plugin: Plugin) {
        plugin.onLoad(this)
        plugins.add(plugin)
    }

    fun removePlugin(plugin : Plugin) {
        plugin.onUnload(this)
        plugins.remove(plugin)
    }
}
