package com.colacelli.ircbot.plugins.rssfeed

import com.colacelli.ircbot.PropertiesPlugin
import com.colacelli.irclib.actors.User
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class RSSFeed : PropertiesPlugin {
    private var properties = Properties()

    private object Singleton {
        val instance = RSSFeed()
    }

    companion object {
        const val PROPERTIES_FILE = "rss_feed.properties"
        const val SUBSCRIBERS_PROPERTY = "subscribers"
        const val SUBSCRIBERS_SEPARATOR = ","

        val instance by lazy {
            Singleton.instance
        }
    }

    fun set(url: String, lastUrl: String) {
        properties = loadProperties(PROPERTIES_FILE)
        if (url != SUBSCRIBERS_PROPERTY) properties.setProperty(url, lastUrl)
        saveProperties(PROPERTIES_FILE, properties)
    }

    fun add(url: String) {
        properties = loadProperties(PROPERTIES_FILE)
        if (url != SUBSCRIBERS_PROPERTY) properties.setProperty(url, "")
        saveProperties(PROPERTIES_FILE, properties)
    }

    fun del(url: String) {
        properties = loadProperties(PROPERTIES_FILE)
        if (url != SUBSCRIBERS_PROPERTY) properties.remove(url)
        saveProperties(PROPERTIES_FILE, properties)
    }

    fun list() : SortedMap<String, String> {
        properties = loadProperties(PROPERTIES_FILE)
        val response = HashMap<String, String>()

        properties.forEach { url, lastUrl ->
            if (url != SUBSCRIBERS_PROPERTY) response[url.toString()] = lastUrl.toString()
        }

        return response.toSortedMap()
    }

    fun subscribers() : ArrayList<String> {
        properties = loadProperties(PROPERTIES_FILE)

        val subscribers = ArrayList<String>()
        properties.getProperty(SUBSCRIBERS_PROPERTY).split(SUBSCRIBERS_SEPARATOR).forEach {
            subscribers.add(it)
        } to ArrayList<String>()

        subscribers.sort()

        return subscribers
    }

    fun subscribe(user : User) {
        val subscribers = subscribers()
        val nick = user.nick.toLowerCase()

        if (subscribers.indexOf(nick) == -1) subscribers.add(nick)

        properties.setProperty(SUBSCRIBERS_PROPERTY, subscribers.joinToString(SUBSCRIBERS_SEPARATOR))
        saveProperties(PROPERTIES_FILE, properties)
    }

    fun unsubscribe(user : User) {
        val subscribers = subscribers()
        val nick = user.nick.toLowerCase()

        subscribers.remove(nick)

        properties.setProperty(SUBSCRIBERS_PROPERTY, subscribers.joinToString(SUBSCRIBERS_SEPARATOR))
        saveProperties(PROPERTIES_FILE, properties)
    }
}