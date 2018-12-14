package com.colacelli.ircbot.plugins.duckduckgosearch

import com.colacelli.ircbot.IRCBot
import com.colacelli.ircbot.Plugin
import com.colacelli.ircbot.listeners.OnChannelCommandListener
import com.colacelli.ircbot.plugins.access.IRCBotAccess
import com.colacelli.ircbot.plugins.help.PluginHelp
import com.colacelli.ircbot.plugins.help.PluginHelper
import com.colacelli.irclib.connection.Connection
import com.colacelli.irclib.messages.ChannelMessage
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.net.URL
import java.util.*

class DuckDuckGoSearchPlugin : Plugin {
    override fun getName(): String {
        return "duck_duck_go_search"
    }

    override fun onLoad(bot: IRCBot) {
        bot.addListener(object : OnChannelCommandListener {
            override fun channelCommand(): String {
                return ".ddgo"
            }

            override fun onChannelCommand(connection: Connection, message: ChannelMessage, command: String, args: Array<String>) {
                if (args.isNotEmpty()) {
                    val query = args.joinToString(" ")
                    val search = DuckDuckGoSearch(query)
                    search.addListener(object : OnDuckDuckGoSearchResultListener {
                        override fun onSuccess(searchResult: DuckDuckGoSearchResult) {
                            val description = searchResult.text.split(".")[0]
                            val text = "[${searchResult.source}] ${searchResult.title}: $description - ${searchResult.url}"
                            connection.send(ChannelMessage(message.channel, text, connection.user))
                        }

                        override fun onError() {
                            connection.send(ChannelMessage(message.channel, "Not found!", connection.user))
                        }
                    })

                    val worker = Thread(search)
                    worker.name = "duck_duck_go_search"
                    worker.start()
                }
            }

        })

        PluginHelper.instance.addHelp(PluginHelp(
                ".ddgo",
                IRCBotAccess.Level.USER,
                "Find on DuckDuckGo (https://duckduckgo.com)",
                "query"))
    }

    override fun onUnload(bot: IRCBot) {
        bot.removeListener(".ddgo")
        PluginHelper.instance.removeHelp(".ddgo")
    }

    private class DuckDuckGoSearch(val query : String) : Runnable {
        private val listeners = ArrayList<OnDuckDuckGoSearchResultListener>()

        companion object {
            const val DUCK_DUCK_GO_URL = "https://api.duckduckgo.com/?q=QUERY&format=json&t=ircbot"
        }

        override fun run() {
            val url = DUCK_DUCK_GO_URL
                    .replace("QUERY", query)

            val stream = URL(url).openStream()
            val scanner = Scanner(stream).useDelimiter("\\A")

            var json = ""
            while (scanner.hasNext()) {
                json += scanner.next()
            }

            val gson = Gson()
            val response = gson.fromJson(json, DuckDuckGoSearchResult::class.java)

            listeners.forEach {
                if (response.url.isNotBlank()) {
                    it.onSuccess(response)
                } else {
                    it.onError()
                }
            }
        }

        fun addListener(listener: OnDuckDuckGoSearchResultListener) {
            listeners.add(listener)
        }
    }

    class DuckDuckGoSearchResult(@SerializedName("Heading") val title: String, @SerializedName("AbstractText") val text: String, @SerializedName("AbstractSource") val source: String, @SerializedName("AbstractURL") val url: String)
}