package com.colacelli.ircbot.plugins.websitetitle

import com.colacelli.ircbot.IRCBot
import com.colacelli.ircbot.Plugin
import com.colacelli.irclib.connection.listeners.OnChannelMessageListener
import com.colacelli.irclib.messages.ChannelMessage
import org.jsoup.Jsoup
import java.util.regex.Pattern

class WebsiteTitlePlugin : Plugin {
    val listener = OnChannelMessageListener { connection, message ->
        val text = message.text
        val urlsPattern = Pattern.compile("((http://|https://)([^ ]+))")
        val urlsMatcher = urlsPattern.matcher(text)

        while (urlsMatcher.find()) {
            val websiteTitle = WebsiteTitle(urlsMatcher.group(0))
            websiteTitle.addListener(object : OnWebsiteTitleGetListener {
                override fun onSuccess(url: String, title: String) {
                    val response = ChannelMessage.Builder()
                            .setSender(connection.user)
                            .setChannel(message.channel)
                            .setText("$title - $url")
                            .build()

                    connection.send(response)
                }

                override fun onError(url: String) {
                }
            })

            val worker = Thread(websiteTitle)
            worker.name = "website_title"
            worker.start()
        }
    }

    override fun getName(): String {
        return "website_title"
    }

    override fun onLoad(bot: IRCBot) {
        bot.addListener(listener)
    }

    override fun onUnload(bot: IRCBot) {
        bot.removeListener(listener)
    }

    private class WebsiteTitle(val url : String) : Runnable {
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