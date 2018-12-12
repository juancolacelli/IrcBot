package com.colacelli.ircbot.plugins.nickserv

import com.colacelli.ircbot.IRCBot
import com.colacelli.ircbot.Plugin
import com.colacelli.irclib.actors.User
import com.colacelli.irclib.connection.listeners.OnConnectListener
import com.colacelli.irclib.messages.PrivateMessage

class NickServPlugin(password : String) : Plugin {
    val listener = OnConnectListener { connection, _, user ->
        val nickServ = User.Builder()
                .setNick("nickserv")
                .build()

        val message = PrivateMessage.Builder()
                .setReceiver(nickServ)
                .setSender(user)
                .setText("identify $password")
                .build()

        connection.send(message)
    }
    override fun getName(): String {
        return "nickserv"
    }

    override fun onLoad(bot: IRCBot) {
        bot.addListener(listener)
    }

    override fun onUnload(bot: IRCBot) {
        bot.removeListener(listener)
    }
}