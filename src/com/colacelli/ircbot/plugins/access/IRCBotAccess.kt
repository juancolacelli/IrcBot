package com.colacelli.ircbot.plugins.access

import com.colacelli.ircbot.IRCBot
import com.colacelli.ircbot.listeners.OnChannelCommandListener
import com.colacelli.ircbot.PropertiesPlugin
import com.colacelli.irclib.actors.User
import com.colacelli.irclib.connection.Connection
import com.colacelli.irclib.connection.Rawable
import com.colacelli.irclib.connection.listeners.OnRawCodeListener
import com.colacelli.irclib.messages.ChannelMessage
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
                // Identified by nickserv?
                val identifiedListener = object : OnRawCodeListener {
                    override fun rawCode(): Int {
                        return Rawable.RawCode.WHOIS_IDENTIFIED_NICK.code
                    }

                    override fun onRawCode(connection1: Connection?, message1: String?, rawCode: Int, vararg args1: String?) {
                        if (args1[3] == message.sender.nick && getAccess(message.sender).level >= level.level) {
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

                    override fun onRawCode(connection1: Connection?, message1: String?, rawCode: Int, vararg args1: String?) {
                        if (args1[3] == message.sender.nick) {
                            bot.removeListener(identifiedListener)
                            bot.removeListener(this)
                        }
                    }

                })

                connection.whois(message.sender)
            }
        })
    }

    fun removeListener(bot: IRCBot, command: String) {
        bot.removeListener(command)
    }

    fun getAccess(user : User) : Level {
        properties = loadProperties(PROPERTIES_FILE)
        val access = properties.getProperty(user.nick.toLowerCase()).toInt()

        return getAccessLevel(access)
    }

    private fun getAccessLevel(access: Int) : Level {
        var level = Level.USER

        if (access > 0) {
            Level.values().forEach {
                if (it.level == access) level = it
            }
        }

        return level
    }

    fun setAccess(nick:String, level:Int) {
        setAccess(nick, getAccessLevel(level))
    }

    fun setAccess(nick:String, level:Level) {
        properties = loadProperties(PROPERTIES_FILE)
        properties.setProperty(nick.toLowerCase(), level.toString())
        saveProperties(PROPERTIES_FILE, properties)
    }

    fun getAccesses() : HashMap<String, Level> {
        properties = loadProperties(PROPERTIES_FILE)

        val accesses = HashMap<String, Level>()
        properties.forEach {
            accesses[it.key.toString()] = getAccessLevel(it.value.toString().toInt())
        }

        return accesses
    }
}
