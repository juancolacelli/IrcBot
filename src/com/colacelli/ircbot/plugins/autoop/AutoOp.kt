package com.colacelli.ircbot.plugins.autoop

import com.colacelli.ircbot.IRCBot
import com.colacelli.ircbot.base.Plugin
import com.colacelli.irclib.actors.Channel
import com.colacelli.irclib.actors.User
import com.colacelli.irclib.connection.Connection
import com.colacelli.irclib.connection.listeners.OnChannelModeListener
import com.colacelli.irclib.connection.listeners.OnJoinListener
import com.colacelli.irclib.messages.PrivateMessage

class AutoOp : Plugin {
    val listeners = arrayOf(
        object : OnJoinListener {
            override fun onJoin(connection: Connection, user: User, channel: Channel) {
                if (user.nick == connection.user.nick) {
                    claimOp(connection, channel)
                }
            }
        },
        object : OnChannelModeListener {
            override fun onChannelMode(connection: Connection, channel: Channel, mode: String, vararg args: String) {
                if (mode.indexOf("-") > -1 && args.indexOf(connection.user.nick) > -1)  {
                    claimOp(connection, channel)
                }
            }
        }
    )

    override var name = "auto_op"
    override fun onLoad(bot: IRCBot) {
        listeners.forEach {
            bot.addListener(it)
        }
    }

    override fun onUnload(bot: IRCBot) {
        listeners.forEach {
            bot.removeListener(it)
        }
    }

    fun claimOp(connection: Connection, channel: Channel) {
        connection.send(PrivateMessage(
                "op ${channel.name}",
                connection.user,
                User("ChanServ")
        ))
    }
}