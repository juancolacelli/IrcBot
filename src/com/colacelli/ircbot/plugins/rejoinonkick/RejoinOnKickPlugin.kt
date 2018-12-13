package com.colacelli.ircbot.plugins.rejoinonkick

import com.colacelli.ircbot.IRCBot
import com.colacelli.ircbot.Plugin
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
    private val listeners : ArrayList<Listener> = ArrayList()

    init {
        listeners.add(OnKickListener { connection, _, channel ->
            connection.join(channel)
        })

        listeners.add(object : OnRawCodeListener {
            override fun rawCode(): Int {
                return Rawable.RawCode.JOIN_BANNED.code
            }

            override fun onRawCode(connection: Connection?, message: String?, rawCode: Int, vararg args: String?) {
                val timer = Timer()
                timer.schedule(object : TimerTask() {
                    override fun run() {
                        val chanServ = User.Builder()
                                .setNick("chanserv")
                                .build()

                        val channel = Channel(args[3])

                        val response = PrivateMessage.Builder()
                                .setSender(connection?.user)
                                .setReceiver(chanServ)
                                .setText("unban ${channel.name}")
                                .build()

                        connection?.send(response)
                        connection?.join(channel)
                    }
                }, 5000)
            }
        })
    }

    override fun getName(): String {
        return "rejoin_on_kick"
    }

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