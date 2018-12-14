package com.colacelli.ircbot

import com.colacelli.ircbot.listeners.OnChannelCommandListener
import com.colacelli.irclib.actors.User
import com.colacelli.irclib.connection.Connection
import com.colacelli.irclib.connection.Server
import com.colacelli.irclib.connection.listeners.Listenable
import com.colacelli.irclib.connection.listeners.Listener
import com.colacelli.irclib.connection.listeners.OnChannelMessageListener
import com.colacelli.irclib.messages.ChannelMessage
import java.util.*

const val HTTP_USER_AGENT = "GNU IRC Bot - https://gitlab.com/jic/ircbot"

class IRCBot(val server: Server, val user: User) : Listenable {
    private val listeners =  HashMap<String, ArrayList<OnChannelCommandListener>>()
    private val connection = Connection(server, user)
    var plugins = ArrayList<Plugin>()

    init {
        System.setProperty("http.agent", HTTP_USER_AGENT)
        addListener(object : OnChannelMessageListener {
            override fun onChannelMessage(connection: Connection, message: ChannelMessage) {
                val words = message.text.split(" ")
                val command = words[0].toLowerCase()

                // Remove the command
                val args = words.drop(1).toTypedArray()

                listeners[command]?.forEach {
                    it.onChannelCommand(connection, message, command, args)
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
        listener.commands.forEach {
            val command = it.toLowerCase()

            val commandListeners = listeners.getOrDefault(command, ArrayList())
            commandListeners.add(listener)

            listeners[command] = commandListeners
        }
    }

    fun removeListener(command: String) {
        listeners[command.toLowerCase()]?.clear()
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
