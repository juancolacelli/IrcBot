package com.colacelli.ircbot.plugins.rssfeed

import com.colacelli.ircbot.IRCBot
import com.colacelli.ircbot.base.Access
import com.colacelli.ircbot.base.AsciiTable
import com.colacelli.ircbot.base.Help
import com.colacelli.ircbot.base.Plugin
import com.colacelli.ircbot.base.listeners.OnChannelCommandListener
import com.colacelli.irclib.actors.User
import com.colacelli.irclib.connection.Connection
import com.colacelli.irclib.connection.listeners.OnPingListener
import com.colacelli.irclib.messages.ChannelMessage
import com.colacelli.irclib.messages.PrivateNoticeMessage
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RSSFeedPlugin : Plugin {
    val rssFeed = RSSFeed()

    val listener = object : OnPingListener {
        override fun onPing(connection: Connection) {
            check(connection)
        }
    }

    override var name = "rss_feed"

    override fun onLoad(bot: IRCBot) {
        bot.addListener(listener)

        bot.addListener(object : OnChannelCommandListener {
            override val command = ".rssSubscribe"
            override val aliases = arrayOf(".rssSub")
            override val level = Access.Level.USER
            override val help = Help(this, "Subscribe to RSS feed")

            override fun onChannelCommand(connection: Connection, message: ChannelMessage, command: String, args: Array<String>) {
                if (rssFeed.subscribe(message.sender!!)) connection.send(PrivateNoticeMessage("Subscribed to RSS feed!", connection.user, message.sender))
            }
        })

        bot.addListener(object : OnChannelCommandListener {
            override val command = ".rssUnsubscribe"
            override val aliases = arrayOf(".rssUnsub")
            override val level = Access.Level.USER
            override val help = Help(this, "Unsubscribe from RSS feed")

            override fun onChannelCommand(connection: Connection, message: ChannelMessage, command: String, args: Array<String>) {
                if (rssFeed.unsubscribe(message.sender!!)) connection.send(PrivateNoticeMessage("Unsubscribed to RSS feed!", connection.user, message.sender))
            }
        })

        bot.addListener(object : OnChannelCommandListener {
            override val command = ".rssAdd"
            override val aliases = arrayOf(".rss+")
            override val level = Access.Level.ADMIN
            override val help = Help(this, "Add a RSS feed", "url")

            override fun onChannelCommand(connection: Connection, message: ChannelMessage, command: String, args: Array<String>) {
                if (args.isNotEmpty()) {
                    if (rssFeed.add(args[0])) connection.send(PrivateNoticeMessage("RSS feed added!", connection.user, message.sender))
                }
            }
        })

        bot.addListener(object : OnChannelCommandListener {
            override val command = ".rssDel"
            override val aliases = arrayOf(".rss-")
            override val level = Access.Level.ADMIN
            override val help = Help(this, "Removes a RSS feed", "url")

            override fun onChannelCommand(connection: Connection, message: ChannelMessage, command: String, args: Array<String>) {
                if (args.isNotEmpty()) {
                    if (rssFeed.del(args[0])) connection.send(PrivateNoticeMessage("RSS feed removed!", connection.user, message.sender))
                }
            }
        })

        bot.addListener(object : OnChannelCommandListener {
            override val command = ".rssList"
            override val aliases = arrayOf(".rss")
            override val level = Access.Level.USER
            override val help = Help(this, "List all available RSS feeds")

            override fun onChannelCommand(connection: Connection, message: ChannelMessage, command: String, args: Array<String>) {
                val rssFeeds = ArrayList<Array<String>>()
                rssFeed.list().forEach { url, _ ->
                    rssFeeds.add(arrayOf(url))
                }

                AsciiTable(arrayOf("URL"), rssFeeds).toText().forEach {
                    connection.send(PrivateNoticeMessage(it, connection.user, message.sender))
                }
            }
        })

        bot.addListener(object : OnChannelCommandListener {
            override val command = ".rssCheck"
            override val aliases = arrayOf(".rss()")
            override val level = Access.Level.OPERATOR
            override val help = Help(this, "Check all available RSS feed")

            override fun onChannelCommand(connection: Connection, message: ChannelMessage, command: String, args: Array<String>) {
                connection.send(PrivateNoticeMessage("Checking...", connection.user, message.sender))
                check(connection)
            }
        })

        bot.addListener(object : OnChannelCommandListener {
            override val command = ".rssSubscribers"
            override val aliases: Nothing? = null
            override val level = Access.Level.OPERATOR
            override val help = Help(this, "List all RSS feed subscribers")

            override fun onChannelCommand(connection: Connection, message: ChannelMessage, command: String, args: Array<String>) {
                val nicks = ArrayList<Array<String>>()
                rssFeed.subscribers().forEach {
                    nicks.add(arrayOf(it))
                }

                AsciiTable(arrayOf("Subscriber"), nicks).toText().forEach {
                    connection.send(PrivateNoticeMessage(it, connection.user, message.sender))
                }
            }
        })
    }

    override fun onUnload(bot: IRCBot) {
        bot.removeListener(listener)
        bot.removeListenersByCommands(arrayOf(".rssSubscribe", ".rssUnsubscribe", ".rssAdd", ".rssDel", ".rssList", ".rssCheck", ".rssSubscribers"))
    }

    private fun check(connection: Connection) {
        GlobalScope.launch {
            val items = rssFeed.check().await()
            items.forEach {
                if (it.hasNewContent) {
                    rssFeed.subscribers().forEach { subscriber ->
                        connection.send(PrivateNoticeMessage(
                                "${it.title} - ${it.url}",
                                connection.user,
                                User(subscriber)
                        ))
                    }
                }
            }
        }
    }
}