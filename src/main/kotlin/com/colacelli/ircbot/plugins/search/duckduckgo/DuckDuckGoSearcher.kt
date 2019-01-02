package com.colacelli.ircbot.plugins.search.duckduckgo

import com.google.gson.Gson
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.net.URL
import java.util.*

class DuckDuckGoSearcher {
    companion object {
        const val DUCK_DUCK_GO_URL = "https://api.duckduckgo.com/?q=QUERY&format=json&t=ircbot"
    }

    fun search(query: String) : Deferred<DuckDuckGoSearchResult?> {
        return GlobalScope.async {
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

            if (response.url.isNotBlank()) {
                response
            } else {
                null
            }
        }
    }
}