package com.colacelli.ircbot.plugins.thepiratebaysearch

import com.colacelli.ircbot.IRCBot
import com.colacelli.ircbot.Plugin
import com.colacelli.ircbot.listeners.OnChannelCommandListener
import com.colacelli.ircbot.plugins.access.IRCBotAccess
import com.colacelli.ircbot.plugins.help.PluginHelp
import com.colacelli.ircbot.plugins.help.PluginHelper
import com.colacelli.irclib.connection.Connection
import com.colacelli.irclib.messages.ChannelMessage
import org.jsoup.Jsoup

class ThePirateBaySearchPlugin : Plugin {
    override fun getName(): String {
        return "the_pirate_bay_search"
    }

    override fun onLoad(bot: IRCBot) {
        bot.addListener(object : OnChannelCommandListener {
            override fun channelCommand(): String {
                return ".torrent"
            }

            override fun onChannelCommand(connection: Connection, message: ChannelMessage, command: String, args: Array<String>) {
                if (args.isNotEmpty()) {
                    val search = ThePirateBaySearch(args.joinToString(" "))

                    search.addListener(object : OnThePirateBaySearchResult {
                        override fun onSuccess(result: ThePirateBaySearchResult) {
                            val text = "${result.title} [↑${result.uploadedAt}][↓${result.size}][⇅${result.seeders}S/${result.leechers}L] ${result.magnet}"
                            val response = ChannelMessage.Builder()
                                    .setSender(connection.user)
                                    .setText(text)
                                    .setChannel(message.channel)
                                    .build()

                            connection.send(response)
                        }

                        override fun onError(result: ThePirateBaySearchResult) {
                            val response = ChannelMessage.Builder()
                                    .setSender(connection.user)
                                    .setText("Not found!")
                                    .setChannel(message.channel)
                                    .build()

                            connection.send(response)
                        }
                    })

                    val worker = Thread(search)
                    worker.name = "the_pirate_bay_search"
                    worker.start()
                }
            }
        })

        PluginHelper.instance.addHelp(PluginHelp(
                ".torrent",
                IRCBotAccess.Level.USER,
                "Find torrents on ThePirateBay (https://thepiratebay.online)",
                "query"))
    }

    override fun onUnload(bot: IRCBot) {
        bot.removeListener(".torrent")
        PluginHelper.instance.removeHelp(".torrent")
    }

    private class ThePirateBaySearch(val query: String) : Runnable {
        private val listeners = ArrayList<OnThePirateBaySearchResult>()

        companion object {
            const val THE_PIRATE_BAY_URL = "https://thepiratebay.online/s/?q=QUERY&page=0&orderby=99"
        }

        override fun run() {
            val url = THE_PIRATE_BAY_URL.replace("QUERY", query)

            val document = Jsoup.connect(url).get()

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
                    it.onError(result)
                }
            }
        }

        fun addListener(listener: OnThePirateBaySearchResult) {
            listeners.add(listener)
        }
    }

    class ThePirateBaySearchResult(val title: String, val description: String, val magnet: String, val uploadedAt: String, val size: String, val seeders:Int, val leechers:Int)
}