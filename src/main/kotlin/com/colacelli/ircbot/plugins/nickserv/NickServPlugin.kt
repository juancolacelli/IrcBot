package com.colacelli.ircbot.plugins.nickserv

import com.colacelli.ircbot.IRCBot
import com.colacelli.ircbot.base.Plugin
import com.colacelli.irclib.actors.User
import com.colacelli.irclib.connection.Connection
import com.colacelli.irclib.connection.Server
import com.colacelli.irclib.connection.listeners.OnConnectListener
import com.colacelli.irclib.messages.PrivateMessage

class NickServPlugin(password: String) : Plugin {
    val listener = object : OnConnectListener {
        override fun onConnect(connection: Connection, server: Server, user: User) {
            return connection.send(PrivateMessage(
                    "identify $password",
                    connection.user,
                    User("NickServ")
            ))
        }
    }
    override var name = "nickserv"

    override fun onLoad(bot: IRCBot) {
        bot.addListener(listener)
    }

    override fun onUnload(bot: IRCBot) {
        bot.removeListener(listener)
    }
}