package com.colacelli.ircbot.plugins.apertiumtranslate

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

class ApertiumTranslatePlugin : Plugin {
    private val listener = object : OnChannelCommandListener {
        override val commands: Array<String>
            get() = arrayOf(".translate", ".trans")

            override fun onChannelCommand(connection: Connection, message: ChannelMessage, command: String, args: Array<String>) {
                if (args.size > 2) {
                    val localeA = args[0]
                    val localeB = args[1]
                    var text = args[2]

                    for (i in 3 until args.size) {
                        text += " ${args[i]}"
                    }

                    val apertiumTranslate = ApertiumTranslate(localeA, localeB, text)

                    apertiumTranslate.addListener(object : OnApertiumTranslateResultListener {
                        override fun onSuccess(translation: ApertiumTranslation) {
                            connection.send(ChannelMessage(
                                    message.channel,
                                    "[${translation.localeA}] ${translation.text} ~ [${translation.localeB}] ${translation.translation}",
                                    connection.user
                            ))
                        }

                        override fun onError() {
                            connection.send(ChannelMessage(message.channel, "Translation not found!", connection.user))
                        }
                    })

                    val worker = Thread(apertiumTranslate)
                    worker.name = "apertium_translate"
                    worker.start()
                }
            }

        }

    override fun getName(): String {
        return "apertium_translate"
    }

    override fun onLoad(bot: IRCBot) {
        bot.addListener(listener)

        PluginHelper.instance.addHelp(PluginHelp(
                ".translate",
                IRCBotAccess.Level.USER,
                "Translate text from locale1 to locale2 using Apertium (https://apertium.org)",
                "locale1",
                "locale2"))
    }

    override fun onUnload(bot: IRCBot) {
        bot.removeListener(".translate")
        PluginHelper.instance.removeHelp(".translate")
    }

    private class ApertiumTranslate(private val localeA: String, private val localeB: String, private val text: String) : Runnable {
        var listeners = ArrayList<OnApertiumTranslateResultListener>()

        companion object {
            const val APERTIUM_URL = "https://www.apertium.org/apy/translate?q=TEXT&langpair=LOCALES"
        }

        override fun run() {
            val url = APERTIUM_URL
                .replace("LOCALES", "$localeA|$localeB")
                .replace("TEXT", text)

            val stream = URL(url).openStream()
            val scanner = Scanner(stream).useDelimiter("\\A")

            var json = ""
            while (scanner.hasNext()) {
                json += scanner.next()
            }

            val gson = Gson()
            val response = gson.fromJson(json, ApertiumTranslationResponse::class.java)

            listeners.forEach {
                if (response.status == 200) {
                    // FIXME: Remove it from the forEach, and try to be assigned by Gson
                    val translation = response.data
                    translation.localeA = localeA
                    translation.localeB = localeB
                    translation.text = text

                    it.onSuccess(translation)
                } else {
                    it.onError()
                }
            }
        }

        fun addListener(listener : OnApertiumTranslateResultListener) {
            listeners.add(listener)
        }
    }

    private class ApertiumTranslationResponse(@SerializedName("responseStatus") val status: Int, @SerializedName("responseData") val data: ApertiumTranslation)
    class ApertiumTranslation(var localeA: String, var localeB: String, var text: String, @SerializedName("translatedText") val translation: String)
}