package com.colacelli.ircbot.plugins.uptime

import com.colacelli.ircbot.IRCBot
import com.colacelli.ircbot.base.Access
import com.colacelli.ircbot.base.Help
import com.colacelli.ircbot.base.Plugin
import com.colacelli.ircbot.base.listeners.OnChannelCommandListener
import com.colacelli.irclib.connection.Connection
import com.colacelli.irclib.messages.ChannelMessage
import java.util.*

class UptimePlugin : Plugin {
    private val startDate = Date(System.currentTimeMillis())
    override var name = "uptime"

    override fun onLoad(bot: IRCBot) {
        bot.addListener(object : OnChannelCommandListener {
            override val command = ".uptime"
            override val aliases = arrayOf(".up")
            override val level = Access.Level.USER
            override val help = Help("Show bot uptime")

            override fun onChannelCommand(connection: Connection, message: ChannelMessage, command: String, args: Array<String>) {
                val currentTimeMillis = System.currentTimeMillis()
                val startMillis = startDate.time

                val diff = currentTimeMillis - startMillis
                val seconds = diff / 1000 % 60
                val minutes = diff / (60 * 1000) % 60
                val hours = diff / (60 * 60 * 1000) % 24
                val days = diff / (60 * 60 * 1000 * 24)

                val uptime = String.format("%dd %02d:%02d:%02d", days, hours, minutes, seconds)
                connection.send(ChannelMessage(message.channel, "Uptime: $uptime", connection.user))
            }

        })
    }

    override fun onUnload(bot: IRCBot) {
        bot.removeListener(".uptime")
    }
}