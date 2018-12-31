package com.colacelli.ircbot.plugins.websitetitle

import com.colacelli.ircbot.IRCBot
import com.colacelli.ircbot.base.Plugin
import com.colacelli.irclib.connection.Connection
import com.colacelli.irclib.connection.listeners.OnChannelMessageListener
import com.colacelli.irclib.messages.ChannelMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class WebsiteTitlePlugin : Plugin {
    val parser = WebsiteParser()

    private val listener = object : OnChannelMessageListener {
        override fun onChannelMessage(connection: Connection, message: ChannelMessage) {
            GlobalScope.launch {
                val text = message.text
                val urlsPattern = Regex("((http://|https://)([^ ]+))")

                urlsPattern.findAll(text).forEach {
                    val url = it.value
                    var title = parser.parseTitle(url).await()

                    if (title == null) {
                        title = "Title not found!"
                    }

                    connection.send(ChannelMessage(
                            message.channel,
                            "$title - $url",
                            connection.user
                    ))
                }
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

}