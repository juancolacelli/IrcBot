package com.colacelli.ircbot.plugins.thepiratebaysearch

import com.colacelli.ircbot.IRCBot
import com.colacelli.ircbot.base.Access
import com.colacelli.ircbot.base.Help
import com.colacelli.ircbot.base.Plugin
import com.colacelli.ircbot.base.listeners.OnChannelCommandListener
import com.colacelli.irclib.connection.Connection
import com.colacelli.irclib.messages.ChannelMessage
import org.jsoup.Jsoup
import java.io.IOException

class ThePirateBaySearchPlugin : Plugin {
    override var name = "the_pirate_bay_search"

    override fun onLoad(bot: IRCBot) {
        bot.addListener(object : OnChannelCommandListener {
            override val command = ".thepiratebay"
            override val aliases = arrayOf(".tpb", ".torrent")
            override val level = Access.Level.USER
            override val help = Help(this, "Find torrents on ThePirateBay (https://thepiratebay.org)", "query")

            override fun onChannelCommand(connection: Connection, message: ChannelMessage, command: String, args: Array<String>) {
                if (args.isNotEmpty()) {
                    val search = ThePirateBaySearch(args.joinToString(" "))

                    search.addListener(object : OnThePirateBaySearchResult {
                        override fun onSuccess(result: ThePirateBaySearchResult) {
                            val text = "${result.title} [↑${result.uploadedAt}][↓${result.size}][⇅${result.seeders}S/${result.leechers}L] ${result.magnet}"
                            connection.send(ChannelMessage(message.channel, text, connection.user))
                        }

                        override fun onError() {
                            connection.send(ChannelMessage(message.channel, "Not found!", connection.user))
                        }
                    })

                    val worker = Thread(search)
                    worker.name = "the_pirate_bay_search"
                    worker.start()
                }
            }
        })
    }

    override fun onUnload(bot: IRCBot) {
        bot.removeListener(".torrent")
    }

    private class ThePirateBaySearch(val query: String) : Runnable {
        private val listeners = ArrayList<OnThePirateBaySearchResult>()

        companion object {
            const val THE_PIRATE_BAY_URL = "https://pirateproxy.app/search/QUERY/0/99/0"
        }

        override fun run() {
            val url = THE_PIRATE_BAY_URL.replace("QUERY", query)

            try {
                val document = Jsoup.connect(url).get()

                // FIXME: Dirty code...
                val firstResult = document.select("table#searchResult tbody td:not(.vertTh)").first()
                val link = firstResult.select("a").first()
                val magnet = firstResult.select("a")[1]
                val description = firstResult.select("font.detDesc").first()
                val parentTr = firstResult.parent()
                val seeders = parentTr.select("td[align=right]").first()
                val leechers = parentTr.select("td[align=right]").last()

                val descriptionText = description.text().split(", ")
                val size = descriptionText[1].replace("Size ", "")
                val uploadedAt = descriptionText[0].replace("Uploaded ", "")
                val magnetLink = magnet.attr("href").split("&")[0]

                val result = ThePirateBaySearchResult(
                        link.text(),
                        link.attr("href"),
                        magnetLink,
                        uploadedAt,
                        size,
                        Integer.parseInt(seeders.text()),
                        Integer.parseInt(leechers.text())
                )

                listeners.forEach {
                    if (result.magnet.isNotBlank()) {
                        it.onSuccess(result)
                    } else {
                        it.onError()
                    }
                }
            } catch (e: IOException) {
                listeners.forEach {
                    it.onError()
                }
            }
        }

        fun addListener(listener: OnThePirateBaySearchResult) {
            listeners.add(listener)
        }
    }

    class ThePirateBaySearchResult(val title: String, val description: String, val magnet: String, val uploadedAt: String, val size: String, val seeders: Int, val leechers: Int)
}