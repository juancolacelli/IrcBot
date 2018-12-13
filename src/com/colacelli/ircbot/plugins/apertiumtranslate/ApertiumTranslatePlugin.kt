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
            override fun channelCommand(): String {
                return ".translate"
            }

            override fun onChannelCommand(connection: Connection, message: ChannelMessage, command: String, args: Array<String>) {
                if (args.size > 2) {
                    val localeA = args[0]
                    val localeB = args[1]
                    var text = args[2]

                    for (i in 3 until args.size) {
                        text += " ${args[i]}"
                    }

                    val apertiumTranslate = ApertiumTranslate(localeA, localeB, text)
                    val response = ChannelMessage.Builder()
                            .setSender(connection.user)
                            .setChannel(message.channel)

                    apertiumTranslate.addListener(object : OnApertiumTranslateResult {
                        override fun onSuccess(translation: ApertiumTranslation) {
                            response.setText("[${translation.localeA}] ${translation.text} ~ [${translation.localeB}] ${translation.translation}")
                            connection.send(response.build())
                        }

                        override fun onError() {
                            response.setText("Translation not found!")
                            connection.send(response.build())
                        }
                    })
                }
            }

        }

    companion object {
        const val APERTIUM_URL = "https://www.apertium.org/apy/translate?q=TEXT&langpair=LOCALES"
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

    class ApertiumTranslate(private val localeA: String, private val localeB: String, private val text: String) : Runnable {
        var listeners = ArrayList<OnApertiumTranslateResult>()

        override fun run() {
            val url = APERTIUM_URL
                .replace("LOCALES", "$localeA|$localeB")
                .replace("TEXT", text)

            val response = URL(url).openStream()
            val scanner = Scanner(response).useDelimiter("\\A")

            var json = ""
            while (scanner.hasNext()) {
                json += scanner.next()
            }

            val gson = Gson()
            val translationResponse = gson.fromJson(json, ApertiumTranslationResponse::class.java)

            listeners.forEach {
                if (translationResponse.status == 200) {
                    // FIXME: Remove it from the forEach, and try to be assigned by Gson
                    val translation = translationResponse.data
                    translation.localeA = localeA
                    translation.localeB = localeB
                    translation.text = text

                    it.onSuccess(translation)
                } else {
                    it.onError()
                }
            }
        }

        fun addListener(listener : OnApertiumTranslateResult) {
            listeners.add(listener)
        }
    }

    class ApertiumTranslationResponse(@SerializedName("responseStatus") val status: Int, @SerializedName("responseData") val data: ApertiumTranslation)
    class ApertiumTranslation(var localeA: String, var localeB: String, var text: String, @SerializedName("translatedText") val translation: String)
}