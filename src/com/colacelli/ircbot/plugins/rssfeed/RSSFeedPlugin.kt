package com.colacelli.ircbot.plugins.rssfeed

import com.colacelli.ircbot.IRCBot
import com.colacelli.ircbot.Plugin
import com.colacelli.ircbot.listeners.OnChannelCommandListener
import com.colacelli.ircbot.plugins.access.IRCBotAccess
import com.colacelli.ircbot.plugins.help.PluginHelp
import com.colacelli.ircbot.plugins.help.PluginHelper
import com.colacelli.irclib.actors.User
import com.colacelli.irclib.connection.Connection
import com.colacelli.irclib.connection.listeners.OnPingListener
import com.colacelli.irclib.messages.ChannelMessage
import com.colacelli.irclib.messages.PrivateNoticeMessage
import org.jsoup.Jsoup
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL
import java.util.*

class RSSFeedPlugin : Plugin {
    val listener = OnPingListener {
        check(it)
    }

    override fun getName(): String {
        return "rss_feed"
    }

    override fun onLoad(bot: IRCBot) {
        bot.addListener(object : OnChannelCommandListener{
            override fun channelCommand(): String {
                return ".rss"
            }

            override fun onChannelCommand(connection: Connection, message: ChannelMessage, command: String, args: Array<String>) {
                val response = PrivateNoticeMessage.Builder()
                        .setSender(connection.user)
                        .setReceiver(message.sender)

                if (args.isNotEmpty()) {
                    when (args[0]) {
                        "subscribe" -> {
                            RSSFeed.instance.subscribe(message.sender)

                            response.setText("Subscribed to RSS feed!")
                            connection.send(response.build())
                        }
                        "unsubscribe" -> {
                            RSSFeed.instance.unsubscribe(message.sender)

                            response.setText("Unsubscribed from RSS feed!")
                            connection.send(response.build())
                        }
                    }
                }
            }

        })

        IRCBotAccess.instance.addListener(bot, IRCBotAccess.Level.ADMIN, object : OnChannelCommandListener{
            override fun channelCommand(): String {
                return ".rss"
            }

            override fun onChannelCommand(connection: Connection, message: ChannelMessage, command: String, args: Array<String>) {
                val response = PrivateNoticeMessage.Builder()
                        .setSender(connection.user)
                        .setReceiver(message.sender)

                if (args.size > 1) {
                    val url = args[1]
                    when (args[0]) {
                        "add" -> {
                            RSSFeed.instance.add(url)

                            response.setText("RSS feed added!")
                            connection.send(response.build())
                        }

                        "del" -> {
                            RSSFeed.instance.del(url)

                            response.setText("RSS feed removed!")
                            connection.send(response.build())
                        }
                    }
                } else {
                    when (args[0]) {
                        "subscribers" -> {
                            val nicks = RSSFeed.instance.subscribers()
                            nicks.sort()

                            response.setText(nicks.joinToString(" "))
                            connection.send(response.build())
                        }

                        "list" -> {
                            RSSFeed.instance.list().forEach { url, _ ->
                                response.setText(url)
                                connection.send(response.build())
                            }
                        }

                        "check" -> {
                            response.setText("Checking...")
                            connection.send(response.build())

                            check(connection)
                        }
                    }
                }
            }
        })

        PluginHelper.instance.addHelp(PluginHelp(
                ".rss check",
                IRCBotAccess.Level.ADMIN,
                "Check all RSS feeds"))

        PluginHelper.instance.addHelp(PluginHelp(
                ".rss list",
                IRCBotAccess.Level.ADMIN,
                "List all RSS feeds"))

        PluginHelper.instance.addHelp(PluginHelp(
                ".rss add",
                IRCBotAccess.Level.ADMIN,
                "Add a RSS feed",
                "url"))

        PluginHelper.instance.addHelp(PluginHelp(
                ".rss del",
                IRCBotAccess.Level.ADMIN,
                "Delete an RSS feed",
                "index"))

        PluginHelper.instance.addHelp(PluginHelp(
                ".rss subscribers",
                IRCBotAccess.Level.ADMIN,
                "List all subscribers"))

        PluginHelper.instance.addHelp(PluginHelp(
                ".rss subscribe",
                IRCBotAccess.Level.USER,
                "Subscribe to RSS feed"))

        PluginHelper.instance.addHelp(PluginHelp(
                ".rss unsubscribe",
                IRCBotAccess.Level.USER,
                "Unsubscribe from RSS feed"))
    }

    override fun onUnload(bot: IRCBot) {
        bot.removeListener(listener)
        bot.removeListener(".rss")
        arrayOf("check", "list", "add", "del", "subscribers", "subscribe", "unsubscribe").forEach {
            PluginHelper.instance.removeHelp(it)
        }
    }

    private fun check(connection:Connection) {
        RSSFeed.instance.list().forEach { url, lastUrl ->
            val checker = RSSFeedChecker(url)

            checker.addListener(object : OnRSSFeedCheckListener {
                override fun onSuccess(rssFeedItem: RSSFeedItem) {
                    if (rssFeedItem.url != lastUrl) {
                        RSSFeed.instance.set(rssFeedItem.rssFeedUrl, rssFeedItem.url)

                        val response = PrivateNoticeMessage.Builder()
                                .setSender(connection.user)
                                .setText("${rssFeedItem.title} - ${rssFeedItem.url}")

                        RSSFeed.instance.subscribers().forEach {
                            response.setReceiver(User(it))

                            connection.send(response.build())
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