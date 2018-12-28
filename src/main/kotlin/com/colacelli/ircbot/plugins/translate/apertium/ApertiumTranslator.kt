package com.colacelli.ircbot.plugins.translate.apertium

import com.google.gson.Gson
import kotlinx.coroutines.*
import java.net.URL
import java.util.*
import kotlin.coroutines.CoroutineContext

class ApertiumTranslator(private val dispatcher: CoroutineContext = Dispatchers.Main) {
    companion object {
        const val APERTIUM_URL = "https://www.apertium.org/apy/translate?q=TEXT&langpair=LOCALES"
    }

    fun translate(localeA: String, localeB: String, text: String) : Deferred<ApertiumTranslation?> {
        val url = APERTIUM_URL
                .replace("LOCALES", "$localeA|$localeB")
                .replace("TEXT", text.replace(" ", "%20"))

        val stream = URL(url).openStream()
        val scanner = Scanner(stream).useDelimiter("\\A")

        var json = ""
        while (scanner.hasNext()) {
            json += scanner.next()
        }

        val gson = Gson()
        val response = gson.fromJson(json, ApertiumResponse::class.java)

        return GlobalScope.async(dispatcher) {
            if (response.status == 200) {
                // FIXME: Assign with Gson
                val translation = response.data
                translation.localeA = localeA
                translation.localeB = localeB
                translation.text = text

                translation
            } else {
                null
            }
        }
    }
}