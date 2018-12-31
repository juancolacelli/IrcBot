package com.colacelli.ircbot.plugins.websitetitle

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import kotlin.coroutines.CoroutineContext

class WebsiteParser {
    fun parseTitle(url: String) : Deferred<String?> {
        return GlobalScope.async {
            val document = Jsoup.connect(url).get()
            val title = document.title()

            if (title.isNotBlank()) {
                title
            } else {
                null
            }
        }
    }
}