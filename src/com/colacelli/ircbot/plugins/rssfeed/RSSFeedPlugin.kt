package com.colacelli.ircbot.plugins.rssfeed

import com.colacelli.ircbot.IRCBot
import com.colacelli.ircbot.base.Plugin
import com.colacelli.ircbot.base.listeners.OnChannelCommandListener
import com.colacelli.ircbot.base.Access
import com.colacelli.ircbot.base.Help
import com.colacelli.irclib.actors.User
import com.colacelli.irclib.connection.Connection
import com.colacelli.irclib.connection.listeners.OnPingListener
import com.colacelli.irclib.messages.ChannelMessage
import com.colacelli.irclib.messages.PrivateNoticeMessage
import org.jsoup.Jsoup
import java.io.IOException
import java.util.*

class RSSFeedPlugin : Plugin {
    val listener = object : OnPingListener {
        override fun onPing(connection: Connection) {
            return check(connection)
        }
    }

    override var name = "rss_feed"

    override fun onLoad(bot: IRCBot) {
        bot.addListener(object : OnChannelCommandListener{
            override var command = ".rssSubscribe"
            override var level = Access.Level.USER
            override var help = Help("Subscribe to RSS feed")

            override fun onChannelCommand(connection: Connection, message: ChannelMessage, command: String, args: Array<String>) {
                RSSFeed.instance.subscribe(message.sender!!)
                connection.send(PrivateNoticeMessage("Subscribed to RSS feed!", connection.user, message.sender))
            }
        })

        bot.addListener(object : OnChannelCommandListener{
            override var command = ".rssUnsubscribe"
            override var level = Access.Level.USER
            override var help = Help("Unsubscribe from RSS feed")

            override fun onChannelCommand(connection: Connection, message: ChannelMessage, command: String, args: Array<String>) {
                RSSFeed.instance.unsubscribe(message.sender!!)
                connection.send(PrivateNoticeMessage("Unsubscribed to RSS feed!", connection.user, message.sender))
            }
        })

        bot.addListener(object : OnChannelCommandListener{
            override var command = ".rssAdd"
            override var level = Access.Level.ADMIN
            override var help = Help("Add a RSS feed", "url")

            override fun onChannelCommand(connection: Connection, message: ChannelMessage, command: String, args: Array<String>) {
                if (args.isNotEmpty()) {
                    RSSFeed.instance.add(args[0])
                    connection.send(PrivateNoticeMessage("RSS feed added!", connection.user, message.sender))
                }
            }
        })

        bot.addListener(object : OnChannelCommandListener{
            override var command = ".rssDel"
            override var level = Access.Level.ADMIN
            override var help = Help("Removes a RSS feed", "url")

            override fun onChannelCommand(connection: Connection, message: ChannelMessage, command: String, args: Array<String>) {
                if (args.isNotEmpty()) {
                    RSSFeed.instance.del(args[0])
                    connection.send(PrivateNoticeMessage("RSS feed removed!", connection.user, message.sender))
                }
            }
        })

        bot.addListener(object : OnChannelCommandListener{
            override var command = ".rssList"
            override var level = Access.Level.ADMIN
            override var help = Help("List all available RSS feeds")

            override fun onChannelCommand(connection: Connection, message: ChannelMessage, command: String, args: Array<String>) {
                RSSFeed.instance.list().forEach { url, _ ->
                    connection.send(PrivateNoticeMessage(url, connection.user, message.sender))
                }
            }
        })

        bot.addListener(object : OnChannelCommandListener{
            override var command = ".rssCheck"
            override var level = Access.Level.ADMIN
            override var help = Help("Check all available RSS feed")

            override fun onChannelCommand(connection: Connection, message: ChannelMessage, command: String, args: Array<String>) {
                connection.send(PrivateNoticeMessage("Checking...", connection.user, message.sender))
                check(connection)
            }
        })

        bot.addListener(object : OnChannelCommandListener{
            override var command = ".rssSubscribers"
            override var level = Access.Level.ADMIN
            override var help = Help("List all RSS feed subscribers")

            override fun onChannelCommand(connection: Connection, message: ChannelMessage, command: String, args: Array<String>) {
                val nicks = RSSFeed.instance.subscribers()
                nicks.sort()
                connection.send(PrivateNoticeMessage(nicks.joinToString(" "), connection.user, message.sender))
            }
        })
    }

    override fun onUnload(bot: IRCBot) {
        bot.removeListener(listener)
        bot.removeListeners( arrayOf(".rssSubscribe", ".rssUnsubscribe", ".rssAdd", ".rssDel", ".rssList", ".rssCheck", ".rssSubscribers"))
    }

    private fun check(connection:Connection) {
        RSSFeed.instance.list().forEach { url, lastUrl ->
            val checker = RSSFeedChecker(url)

            checker.addListener(object : OnRSSFeedCheckListener {
                override fun onSuccess(rssFeedItem: RSSFeedItem) {
                    if (rssFeedItem.url != lastUrl) {
                        RSSFeed.instance.set(rssFeedItem.rssFeedUrl, rssFeedItem.url)
                        RSSFeed.instance.subscribers().forEach {
                            connection.send(PrivateNoticeMessage(
                                    "${rssFeedItem.title} - ${rssFeedItem.url}",
                                    connection.user,
                                    User(it)
                            ))
                        }
                    }
                }

                override fun onError(url: String) {
                    RSSFeed.instance.del(url)
                }
            })

            val worker = Thread(checker)
            worker.name = "rss_feed_checker"
            worker.start()
        }
    }

    private class RSSFeedChecker(val url: String) : Runnable {
        val listeners = ArrayList<OnRSSFeedCheckListener>()

        override fun run() {
            try {
                val document = Jsoup.connect(url).get()
                val lastItem = document.selectFirst("item")
                val title = lastItem.selectFirst("title").text()
                val link = lastItem.selectFirst("link").text()

                val rssFeedItem = RSSFeedItem(url, link, title)

                listeners.forEach {
                    it.onSuccess(rssFeedItem)
                }
            } catch (e : IOException) {
                listeners.forEach {
                    it.onError(url)
                }
            }
        }

        fun addListener(listener : OnRSSFeedCheckListener) {
            listeners.add(listener)
        }
    }
    class RSSFeedItem(val rssFeedUrl: String, val url:String, val title:String)
}