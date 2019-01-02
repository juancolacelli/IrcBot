package com.colacelli.ircbot.plugins.torrent

import com.colacelli.ircbot.IRCBot
import com.colacelli.ircbot.base.Access
import com.colacelli.ircbot.base.Help
import com.colacelli.ircbot.base.Plugin
import com.colacelli.ircbot.base.listeners.OnChannelCommandListener
import com.colacelli.ircbot.plugins.torrent.thepiratebay.ThePirateBaySearcher
import com.colacelli.irclib.connection.Connection
import com.colacelli.irclib.messages.ChannelMessage
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class TorrentPlugin : Plugin {
    val searcher = ThePirateBaySearcher()

    override var name = "torrent"

    override fun onLoad(bot: IRCBot) {
        bot.addListener(object : OnChannelCommandListener {
            override val command = ".torrent"
            override val aliases = arrayOf(".tpb")
            override val level = Access.Level.USER
            override val help = Help(this, "Search torrents on ThePirateBay (https://thepiratebay.org)", "query")

            override fun onChannelCommand(connection: Connection, message: ChannelMessage, command: String, args: Array<String>) {
                GlobalScope.launch {
                    if (args.isNotEmpty()) {
                        val result = searcher.search(args.joinToString(" ")).await()

                        if (result != null) {
                            val text = "${result.title} [↑${result.uploadedAt}][↓${result.size}][⇅${result.seeders}S/${result.leechers}L] ${result.magnet}"
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
        bot.removeListenerByCommand(".torrent")
    }
}