package com.colacelli.ircbot.plugins.rejoinonkick

import com.colacelli.ircbot.IRCBot
import com.colacelli.ircbot.base.Plugin
import com.colacelli.irclib.actors.Channel
import com.colacelli.irclib.actors.User
import com.colacelli.irclib.connection.Connection
import com.colacelli.irclib.connection.Rawable
import com.colacelli.irclib.connection.listeners.Listener
import com.colacelli.irclib.connection.listeners.OnKickListener
import com.colacelli.irclib.connection.listeners.OnRawCodeListener
import com.colacelli.irclib.messages.PrivateMessage
import java.util.*

class RejoinOnKickPlugin : Plugin {
    private val listeners: ArrayList<Listener> = ArrayList()

    init {
        listeners.add(object : OnKickListener {
            override fun onKick(connection: Connection, user: User, channel: Channel) {
                return connection.join(channel)
            }
        })

        // TODO: Add other blocking entrance modes like +i, +l, etc.
        listeners.add(object : OnRawCodeListener {
            override fun rawCode(): Int {
                return Rawable.RawCode.JOIN_BANNED.code
            }

            override fun onRawCode(connection: Connection, message: String, rawCode: Int, args: List<String>) {
                val timer = Timer()
                timer.schedule(object : TimerTask() {
                    override fun run() {
                        val channel = Channel(args[3])

                        connection.send(PrivateMessage(
                                "unban ${channel.name}",
                                connection.user,
                                User("ChanServ")
                        ))
                        connection.join(channel)
                    }
                }, 5000)
            }
        })
    }

    override var name = "rejoin_on_kick"

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
}