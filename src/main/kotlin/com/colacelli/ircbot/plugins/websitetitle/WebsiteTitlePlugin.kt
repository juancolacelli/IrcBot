package com.colacelli.ircbot.plugins.websitetitle

import com.colacelli.ircbot.IRCBot
import com.colacelli.ircbot.base.Plugin
import com.colacelli.irclib.connection.Connection
import com.colacelli.irclib.connection.listeners.OnChannelMessageListener
import com.colacelli.irclib.messages.ChannelMessage
import org.jsoup.Jsoup

class WebsiteTitlePlugin : Plugin {
    val listener = object : OnChannelMessageListener {
        override fun onChannelMessage(connection: Connection, message: ChannelMessage) {
            val text = message.text
            val urlsPattern = Regex("((http://|https://)([^ ]+))")

            urlsPattern.findAll(text).forEach {
                val websiteTitle = WebsiteTitle(it.value)
                websiteTitle.addListener(object : OnWebsiteTitleGetListener {
                    override fun onSuccess(url: String, title: String) {
                        connection.send(ChannelMessage(
                                message.channel,
                                "$title - $url",
                                connection.user
                        ))
                    }

                    override fun onError(url: String) {
                    }
                })

                val worker = Thread(websiteTitle)
                worker.name = "website_title"
                worker.start()
            }
        }
    }

    override var name = "website_title"

    override fun onLoad(bot: IRCBot) {
        bot.addListener(listener)
    }

    override fun onUnload(bot: IRCBot) {
        bot.removeListener(listener)
    }

    private class WebsiteTitle(val url: String) : Runnable {
        val listeners = ArrayList<OnWebsiteTitleGetListener>()

        override fun run() {
            val document = Jsoup.connect(url).get()
            val title = document.title()

            listeners.forEach {
                if (title.isNotBlank()) {
                    it.onSuccess(url, title)
                } else {
                    it.onError(url)
                }
            }
        }

        fun addListener(listener: OnWebsiteTitleGetListener) {
            listeners.add(listener)
        }
    }
}