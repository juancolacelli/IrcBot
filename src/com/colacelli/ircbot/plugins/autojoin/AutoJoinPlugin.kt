package com.colacelli.ircbot.plugins.autojoin

import com.colacelli.ircbot.IRCBot
import com.colacelli.ircbot.Plugin
import com.colacelli.irclib.actors.Channel
import com.colacelli.irclib.connection.listeners.OnConnectListener

class AutoJoinPlugin(channels : ArrayList<Channel>) : Plugin {
    private var listener = OnConnectListener { connection, _, _ ->
        channels.forEach {
            connection.join(it)
        }
    }

    override fun getName(): String {
        return "auto_join"
    }

    override fun onLoad(bot: IRCBot) {
        bot.addListener(listener)
    }

    override fun onUnload(bot: IRCBot) {
        bot.removeListener(listener)
    }
}