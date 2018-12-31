package com.colacelli.ircbot.plugins.translate

import com.colacelli.ircbot.IRCBot
import com.colacelli.ircbot.base.Access
import com.colacelli.ircbot.base.Help
import com.colacelli.ircbot.base.Plugin
import com.colacelli.ircbot.base.listeners.OnChannelCommandListener
import com.colacelli.ircbot.plugins.translate.apertium.ApertiumTranslator
import com.colacelli.irclib.connection.Connection
import com.colacelli.irclib.messages.ChannelMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class TranslatePlugin : Plugin {
    val translator = ApertiumTranslator()

    override var name = "translate"

    override fun onLoad(bot: IRCBot) {
        bot.addListener(object : OnChannelCommandListener {
            override val command = ".translate"
            override var aliases = arrayOf(".tra")
            override val level = Access.Level.USER
            override val help = Help(this,
                    "Translate text from locale1 to locale2 using Apertium (https://apertium.org)",
                    "locale1", "locale2", "text"
            )

            override fun onChannelCommand(connection: Connection, message: ChannelMessage, command: String, args: Array<String>) {
                if (args.size > 2) {
                    GlobalScope.launch {
                        val localeA = args[0]
                        val localeB = args[1]
                        var text = args[2]

                        for (i in 3 until args.size) {
                            text += " ${args[i]}"
                        }

                        val translation = translator.translate(localeA, localeB, text).await()

                        if (translation == null) {
                            connection.send(ChannelMessage(message.channel, "Translation not found!", connection.user))
                        } else {
                            connection.send(ChannelMessage(
                                    message.channel,
                                    "[${translation.localeA}] ${translation.text} ~ [${translation.localeB}] ${translation.translation}",
                                    connection.user
                            ))
                        }
                    }
                }
            }
        })
    }

    override fun onUnload(bot: IRCBot) {
        bot.removeListener(".translate")
    }

}