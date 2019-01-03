package com.colacelli.ircbot.plugins.torrent.thepiratebay

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.jsoup.Jsoup
import java.io.IOException

class ThePirateBaySearcher {
    companion object {
        const val THE_PIRATE_BAY_URL = "https://pirateproxy.app/search/QUERY/0/99/0"
    }

    fun search(query: String): Deferred<ThePirateBaySearchResult?> {
        return GlobalScope.async {
            val url = THE_PIRATE_BAY_URL.replace("QUERY", query)

            try {
                val document = Jsoup.connect(url).get()

                // FIXME: Dirty code...
                val firstResult = document.select("table#searchResult tbody td:not(.vertTh)").first()
                val link = firstResult.select("a").first()
                val magnet = firstResult.select("a")[1]
                val description = firstResult.select("font.detDesc").first()
                val parentTr = firstResult.parent()
                val seeders = parentTr.select("td[align=right]").first()
                val leechers = parentTr.select("td[align=right]").last()

                val descriptionText = description.text().split(", ")
                val size = descriptionText[1].replace("Size ", "")
                val uploadedAt = descriptionText[0].replace("Uploaded ", "")
                val magnetLink = magnet.attr("href").split("&")[0]

                val result = ThePirateBaySearchResult(
                        link.text(),
                        link.attr("href"),
                        magnetLink,
                        uploadedAt,
                        size,
                        Integer.parseInt(seeders.text()),
                        Integer.parseInt(leechers.text())
                )

                if (result.magnet.isNotBlank()) {
                    result
                } else {
                    null
                }
            } catch (e: IOException) {
                null
            }
        }
    }
}