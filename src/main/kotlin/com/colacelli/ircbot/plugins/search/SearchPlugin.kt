package com.colacelli.ircbot.plugins.search

import com.colacelli.ircbot.IRCBot
import com.colacelli.ircbot.base.Access
import com.colacelli.ircbot.base.Help
import com.colacelli.ircbot.base.Plugin
import com.colacelli.ircbot.base.listeners.OnChannelCommandListener
import com.colacelli.ircbot.plugins.search.duckduckgo.DuckDuckGoSearchResult
import com.colacelli.ircbot.plugins.search.duckduckgo.DuckDuckGoSearcher
import com.colacelli.irclib.connection.Connection
import com.colacelli.irclib.messages.ChannelMessage
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SearchPlugin : Plugin {
    val searcher = DuckDuckGoSearcher()

    override var name = "search"

    override fun onLoad(bot: IRCBot) {
        bot.addListener(object : OnChannelCommandListener {
            override val command = ".search"
            override val aliases = arrayOf(".ddgo")
            override val level = Access.Level.USER
            override val help = Help(this, "Search on DuckDuckGo (https://duckduckgo.com)", "query")

            override fun onChannelCommand(connection: Connection, message: ChannelMessage, command: String, args: Array<String>) {
                if (args.isNotEmpty()) {
                    GlobalScope.launch {
                        val query = args.joinToString(" ")
                        val result = searcher.search(query).await()

                        if (result != null) {
                            val description = result.text.split(".")[0]
                            val text = "[${result.source}] ${result.title}: $description - ${result.url}"
                            connection.send(ChannelMessage(message.channel, text, connection.user))
                        } else {
                            connection.send(ChannelMessage(message.channel, "Not found!", connection.user))
                        }
                    }
                }
            }

        })
    }

    override fun onUnload(bot: IRCBot) {
        bot.removeListener(".search")
    }

}