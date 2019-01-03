package com.colacelli.ircbot.plugins.search.duckduckgo

import com.google.gson.Gson
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class DuckDuckGoSearchResultTest {
    private val json = """{
            "Heading": "GNU",
            "AbstractText": "GNU is not UNIX",
            "AbstractSource": "Wikipedia",
            "AbstractURL": "https://gnu.org"
        }
    """.trimIndent()
    private val result = Gson().fromJson(json, DuckDuckGoSearchResult::class.java)

    @Test
    fun getTitle() {
        assertEquals("GNU", result.title)
    }

    @Test
    fun getText() {
        assertEquals("GNU is not UNIX", result.text)
    }

    @Test
    fun getSource() {
        assertEquals("Wikipedia", result.source)
    }

    @Test
    fun getUrl() {
        assertEquals("https://gnu.org", result.url)
    }
}