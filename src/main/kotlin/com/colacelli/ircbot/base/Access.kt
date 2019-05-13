package com.colacelli.ircbot.base

import com.colacelli.ircbot.IRCBot
import com.colacelli.ircbot.base.listeners.OnAccessCheckListener
import com.colacelli.irclib.actors.User
import com.colacelli.irclib.connection.Connection
import com.colacelli.irclib.connection.Rawable
import com.colacelli.irclib.connection.listeners.OnRawCodeListener
import java.util.*
import kotlin.collections.HashMap

class Access(private val bot: IRCBot) : PropertiesPlugin {
    private var properties = loadProperties(PROPERTIES_FILE)
    var checkWithNickServ = true

    constructor(bot: IRCBot, injectedProperties: Properties) : this(bot) {
        properties = injectedProperties
    }

    enum class Level(val value: Int) {
        ROOT(3),
        ADMIN(2),
        OPERATOR(1),
        USER(0)
    }

    companion object {
        const val PROPERTIES_FILE = "access.properties"
    }

    fun check(user: User, level: Level, listener: OnAccessCheckListener) {
        val userLevel = get(user)

        // Requires level?
        if (level == Level.USER) {
            listener.onSuccess(user, userLevel)
        } else {
            if (userLevel.value >= level.value) {
                if (checkWithNickServ) {
                    var whoisListener: OnRawCodeListener? = null

                    // Identified by NickServ?
                    val identifiedListener = object : OnRawCodeListener {
                        override fun rawCode(): Int {
                            return Rawable.RawCode.WHOIS_IDENTIFIED_NICK.code
                        }

                        override fun onRawCode(connection: Connection, message: String, rawCode: Int, args: List<String>) {
                            if (args[3] == user.nick) {
                                bot.removeListener(this)
                                bot.removeListener(whoisListener!!)

                                listener.onSuccess(user, userLevel)
                            }
                        }
                    }

                    // End of whois?
                    whoisListener = object : OnRawCodeListener {
                        override fun rawCode(): Int {
                            return Rawable.RawCode.WHOIS_END.code
                        }

                        override fun onRawCode(connection: Connection, message: String, rawCode: Int, args: List<String>) {
                            if (args[3] == user.nick) {
                                bot.removeListener(this)
                                bot.removeListener(identifiedListener)

                                listener.onError(user, userLevel)
                            }
                        }
                    }

                    bot.addListener(identifiedListener)
                    bot.addListener(whoisListener)
                } else {
                    listener.onSuccess(user, userLevel)
                }
            } else {
                listener.onError(user, userLevel)
            }

            bot.connection.whois(user)
        }
    }

    fun get(user: User): Level {
        return Level.valueOf(properties.getProperty(user.nick.toLowerCase(), Level.USER.toString()))
    }

    fun add(nick: String, level: Level) {
        properties.setProperty(nick.toLowerCase(), level.toString())
        saveProperties(PROPERTIES_FILE, properties)
    }

    fun del(nick: String) {
        properties.remove(nick.toLowerCase())
        saveProperties(PROPERTIES_FILE, properties)
    }

    fun list(): SortedMap<String, Level> {
        val accesses = HashMap<String, Level>()
        properties.forEach {
            accesses[it.key.toString()] = Level.valueOf(it.value.toString())
        }

        return accesses.toSortedMap()
    }
}
