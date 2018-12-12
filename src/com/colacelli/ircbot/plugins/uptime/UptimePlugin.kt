package com.colacelli.ircbot.plugins.uptime

import com.colacelli.ircbot.IRCBot
import com.colacelli.ircbot.Plugin
import com.colacelli.ircbot.listeners.OnChannelCommandListener
import com.colacelli.ircbot.plugins.access.IRCBotAccess
import com.colacelli.ircbot.plugins.help.PluginHelp
import com.colacelli.ircbot.plugins.help.PluginHelper
import com.colacelli.irclib.connection.Connection
import com.colacelli.irclib.messages.ChannelMessage
import java.util.*

class UptimePlugin : Plugin {
    private val startDate = Date(System.currentTimeMillis())
    override fun getName(): String {
        return "uptime"
    }

    override fun onLoad(bot: IRCBot) {
        bot.addListener(object : OnChannelCommandListener {
            override fun channelCommand(): String {
                return ".uptime"
            }

            override fun onChannelCommand(connection: Connection, message: ChannelMessage, command: String, args: Array<String>) {
                val currentTimeMillis = System.currentTimeMillis()
                val startMillis = startDate.time

                val diff = currentTimeMillis - startMillis
                val seconds = diff / 1000 % 60
                val minutes = diff / (60 * 1000) % 60
                val hours = diff / (60 * 60 * 1000) % 24
                val days = diff / (60 * 60 * 1000 * 24)

                val uptime = String.format("%dd %02d:%02d:%02d", days, hours, minutes, seconds)

                val builder = ChannelMessage.Builder()
                        .setSender(connection.user)
                        .setChannel(message.channel)
                        .setText("Uptime: $uptime")

                connection.send(builder.build())
            }

        })

        PluginHelper.instance.addHelp(PluginHelp(
                ".uptime",
                IRCBotAccess.Level.USER,
                "Shows bot uptime"))
    }

    override fun onUnload(bot: IRCBot) {
        bot.removeListener(".uptime")
        PluginHelper.instance.removeHelp(".uptime")
    }
}