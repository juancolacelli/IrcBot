package com.colacelli.ircbot.plugins.search.duckduckgo

import com.colacelli.ircbot.IRCBot
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll

internal class DuckDuckGoSearcherTest {
    private val searcher = DuckDuckGoSearcher()

    companion object {
        @BeforeAll
        @JvmStatic
        internal fun setUserAgent() {
            System.setProperty("http.agent", IRCBot.HTTP_USER_AGENT)
        }
    }

    @Test
    fun search() {
        runBlocking {
            val result = searcher.search("gnu").await()
            assertNotNull(result)
            assertEquals("GNU", result?.title)
            assertEquals("", result?.text)
            assertEquals("Wikipedia", result?.source)
            assertEquals("https://en.wikipedia.org/wiki/GNU_(disambiguation)", result?.url)
        }
    }
}