package com.colacelli.ircbot.plugins.rssfeed

import com.colacelli.ircbot.base.PropertiesPlugin
import com.colacelli.irclib.actors.User
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.jsoup.Jsoup
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class RSSFeed() : PropertiesPlugin {
    private var properties = loadProperties(PROPERTIES_FILE)

    constructor(injectedProperties: Properties) : this() {
        properties = injectedProperties
    }

    companion object {
        const val PROPERTIES_FILE = "rss_feed.properties"
        const val SUBSCRIBERS_PROPERTY = "subscribers"
        const val SUBSCRIBERS_SEPARATOR = ","
    }

    fun set(url: String, lastUrl: String) : Boolean {
        return if (url != SUBSCRIBERS_PROPERTY) {
            properties.setProperty(url, lastUrl)
            saveProperties(PROPERTIES_FILE, properties)
            true
        } else {
            false
        }
    }

    fun add(url: String) : Boolean {
        return if (url != SUBSCRIBERS_PROPERTY) {
            properties.setProperty(url, "")
            saveProperties(PROPERTIES_FILE, properties)
            true
        } else {
            false
        }
    }

    fun del(url: String) : Boolean {
        return if (url != SUBSCRIBERS_PROPERTY) {
            properties.remove(url)
            saveProperties(PROPERTIES_FILE, properties)
            true
        } else {
            false
        }
    }

    fun list(): SortedMap<String, String> {
        val response = HashMap<String, String>()

        properties.forEach { url, lastUrl ->
            if (url != SUBSCRIBERS_PROPERTY) response[url.toString()] = lastUrl.toString()
        }

        return response.toSortedMap()
    }

    fun subscribers(): ArrayList<String> {
        val subscribers = ArrayList<String>()
        properties.getProperty(SUBSCRIBERS_PROPERTY).split(SUBSCRIBERS_SEPARATOR).forEach {
            subscribers.add(it)
        } to ArrayList<String>()

        subscribers.sort()

        return subscribers
    }

    fun subscribe(user: User) : Boolean {
        val subscribers = subscribers()
        val nick = user.nick.toLowerCase()

        return if (subscribers.indexOf(nick) == -1) {
            subscribers.add(nick)
            properties.setProperty(SUBSCRIBERS_PROPERTY, subscribers.joinToString(SUBSCRIBERS_SEPARATOR))
            saveProperties(PROPERTIES_FILE, properties)
            true
        } else {
            false
        }
    }

    fun unsubscribe(user: User) : Boolean {
        val subscribers = subscribers()
        val nick = user.nick.toLowerCase()

        return if (subscribers.indexOf(nick) > -1) {
            subscribers.remove(nick)
            properties.setProperty(SUBSCRIBERS_PROPERTY, subscribers.joinToString(SUBSCRIBERS_SEPARATOR))
            saveProperties(PROPERTIES_FILE, properties)
            true
        } else {
            false
        }
    }

    private fun check(url: String) : Deferred<RSSFeedItem?> {
        return GlobalScope.async {
            try {
                val document = Jsoup.connect(url).get()
                val lastItem = document.selectFirst("item")
                val title = lastItem.selectFirst("title").text()
                val link = lastItem.selectFirst("link").text()

                RSSFeedItem(url, link, title)
            } catch (e: IOException) {
                null
            }
        }
    }

    fun check() : Deferred<ArrayList<RSSFeedItem>> {
        val items = ArrayList<RSSFeedItem>()

        return GlobalScope.async {
            list().forEach {
                val item = check(it.key).await()

                if (item != null) {
                    item.hasNewContent = it.value != item.url
                    items.add(item)

                    set(it.key, item.url)
                } else {
                    del(it.key)
                }
            }

            items
        }
    }
}