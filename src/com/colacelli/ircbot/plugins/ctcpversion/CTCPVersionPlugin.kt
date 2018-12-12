package com.colacelli.ircbot.plugins.ctcpversion

import com.colacelli.ircbot.IRCBot
import com.colacelli.ircbot.Plugin
import com.colacelli.irclib.connection.listeners.OnCtcpListener
import com.colacelli.irclib.messages.CTCPMessage

class CTCPVersionPlugin(response : String) : Plugin {
    val listener = OnCtcpListener { connection, message, _ ->
        when (message.text) {
            "VERSION" -> {
                val ctcpResponse = CTCPMessage.Builder()
                    .setCommand("VERSION")
                    .setReceiver(message.sender)
                    .setText(response)
                    .build()

                connection.send(ctcpResponse)
            }
        }
    }
    override fun getName(): String {
        return "ctcp_version"
    }

    override fun onLoad(bot: IRCBot) {
        bot.addListener(listener)
    }

    override fun onUnload(bot: IRCBot) {
        bot.removeListener(listener)
    }
}