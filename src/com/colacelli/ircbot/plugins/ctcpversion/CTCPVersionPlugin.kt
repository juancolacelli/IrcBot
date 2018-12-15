package com.colacelli.ircbot.plugins.ctcpversion

import com.colacelli.ircbot.IRCBot
import com.colacelli.ircbot.base.Plugin
import com.colacelli.irclib.connection.Connection
import com.colacelli.irclib.connection.listeners.OnCTCPListener
import com.colacelli.irclib.messages.CTCPMessage

class CTCPVersionPlugin(response: String) : Plugin {
    val listener = object : OnCTCPListener {
        override fun onCTCP(connection: Connection, message: CTCPMessage, vararg args: List<String>) {
            when (message.text) {
                "VERSION" -> {
                    connection.send(CTCPMessage(response, message.text, connection.user, message.sender))
                }
            }
        }
    }

    override var name = "ctcp_version"

    override fun onLoad(bot: IRCBot) {
        bot.addListener(listener)
    }

    override fun onUnload(bot: IRCBot) {
        bot.removeListener(listener)
    }
}