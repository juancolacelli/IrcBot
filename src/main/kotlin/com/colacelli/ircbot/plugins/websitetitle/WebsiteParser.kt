package com.colacelli.ircbot.plugins.websitetitle

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.jsoup.Jsoup

class WebsiteParser {
    fun parseTitle(url: String): Deferred<String?> {
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