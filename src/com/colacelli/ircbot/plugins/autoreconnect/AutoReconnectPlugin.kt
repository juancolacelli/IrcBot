package com.colacelli.ircbot.plugins.autoreconnect

import com.colacelli.ircbot.IRCBot
import com.colacelli.ircbot.Plugin
import com.colacelli.irclib.connection.listeners.OnDisconnectListener

class AutoReconnectPlugin : Plugin {
    private var listener = OnDisconnectListener { connection, server ->
        connection.connect(server, connection.user)
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