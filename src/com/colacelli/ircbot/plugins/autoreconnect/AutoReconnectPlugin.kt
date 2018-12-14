package com.colacelli.ircbot.plugins.autoreconnect

import com.colacelli.ircbot.IRCBot
import com.colacelli.ircbot.Plugin
import com.colacelli.irclib.connection.Connection
import com.colacelli.irclib.connection.Server
import com.colacelli.irclib.connection.listeners.OnDisconnectListener

class AutoReconnectPlugin : Plugin {
    private var listener = object : OnDisconnectListener {
        override fun onDisconnect(connection: Connection, server: Server) {
            return connection.connect()
        }
    }

    override fun getName(): String {
        return "auto_reconnect"
    }

    override fun onLoad(bot: IRCBot) {
        bot.addListener(listener)
    }

    override fun onUnload(bot: IRCBot) {
        bot.removeListener(listener)
    }
}