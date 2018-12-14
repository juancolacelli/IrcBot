package com.colacelli.ircbot.plugins.access

import com.colacelli.ircbot.IRCBot
import com.colacelli.ircbot.listeners.OnChannelCommandListener
import com.colacelli.ircbot.PropertiesPlugin
import com.colacelli.irclib.actors.User
import com.colacelli.irclib.connection.Connection
import com.colacelli.irclib.connection.Rawable
import com.colacelli.irclib.connection.listeners.OnRawCodeListener
import com.colacelli.irclib.messages.ChannelMessage
import com.colacelli.irclib.messages.PrivateNoticeMessage
import java.util.*
import kotlin.collections.HashMap

class IRCBotAccess : PropertiesPlugin {
    var properties = Properties()

    enum class Level(val level : Int) {
        ROOT(3),
        ADMIN(2),
        OPERATOR(1),
        USER(0)
    }

    private object Singleton {
        val instance = IRCBotAccess()
    }

    companion object {
        const val PROPERTIES_FILE = "access.properties"

        val instance by lazy {
            Singleton.instance
        }
    }

    fun addListener(bot : IRCBot, level : Level, listener: OnChannelCommandListener) {
        bot.addListener(object : OnChannelCommandListener {
            override fun channelCommand(): String {
                return listener.channelCommand()
            }

            override fun onChannelCommand(connection: Connection, message: ChannelMessage, command: String, args: Array<String>) {
                if (get(message.sender!!).level >= level.level) {
                    // Identified by nickserv?
                    val identifiedListener = object : OnRawCodeListener {
                        override fun rawCode(): Int {
                            return Rawable.RawCode.WHOIS_IDENTIFIED_NICK.code
                        }

                        override fun onRawCode(connection: Connection, message1: String, rawCode: Int, args1: List<String>) {
                            if (args1[3] == message.sender?.nick) {
                                listener.onChannelCommand(connection, message, command, args)
                            }
                        }

                    }
                    bot.addListener(identifiedListener)

                    // End of whois?
                    bot.addListener(object : OnRawCodeListener {
                        override fun rawCode(): Int {
                            return Rawable.RawCode.WHOIS_END.code
                        }

                        override fun onRawCode(connection: Connection, message1: String, rawCode: Int, args1: List<String>) {
                            if (args1[3] == message.sender?.nick) {
                                bot.removeListener(identifiedListener)
                                bot.removeListener(this)
                            }
                        }

                    })

                    connection.whois(message.sender!!)
                } else {
                    val response = PrivateNoticeMessage("You don't have access to that command!", connection.user, message.sender)
                    connection.send(response)
                }
            }
        })
    }

    fun removeListener(bot: IRCBot, command: String) {
        bot.removeListener(command)
    }

    fun get(user : User) : Level {
        properties = loadProperties(PROPERTIES_FILE)

        return Level.valueOf(properties.getProperty(user.nick.toLowerCase()))
    }

    fun add(nick:String, level:Level) {
        properties = loadProperties(PROPERTIES_FILE)
        properties.setProperty(nick.toLowerCase(), level.toString())
        saveProperties(PROPERTIES_FILE, properties)
    }

    fun del(nick:String) {
        properties = loadProperties(PROPERTIES_FILE)
        properties.remove(nick.toLowerCase())
        saveProperties(PROPERTIES_FILE, properties)
    }

    fun list() : SortedMap<String, Level> {
        properties = loadProperties(PROPERTIES_FILE)

        val accesses = HashMap<String, Level>()
        properties.forEach {
            accesses[it.key.toString()] = Level.valueOf(it.value.toString())
        }

        return accesses.toSortedMap()
    }
}
