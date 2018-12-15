package com.colacelli.ircbot.plugins.autojoin

import com.colacelli.ircbot.IRCBot
import com.colacelli.ircbot.base.Plugin
import com.colacelli.irclib.actors.Channel
import com.colacelli.irclib.actors.User
import com.colacelli.irclib.connection.Connection
import com.colacelli.irclib.connection.Server
import com.colacelli.irclib.connection.listeners.OnConnectListener

class AutoJoinPlugin(channels : ArrayList<Channel>) : Plugin {
    private var listener = object : OnConnectListener {
        override fun onConnect(connection: Connection, server: Server, user: User) {
            return channels.forEach {
                connection.join(it)
            }
        }
    }

    override var name = "auto_join"

    override fun onLoad(bot: IRCBot) {
        bot.addListener(listener)
    }

    override fun onUnload(bot: IRCBot) {
        bot.removeListener(listener)
    }
}