package com.colacelli.ircbot.plugins.search.duckduckgo

import com.colacelli.ircbot.IRCBot
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

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
    fun search() = runBlocking {
        withTimeout(10000L) {
            val result = searcher.search("gnu").await()
            assertNotNull(result)
            assertEquals("GNU", result?.title)
            assertEquals("", result?.text)
            assertEquals("Wikipedia", result?.source)
            assertEquals("https://en.wikipedia.org/wiki/GNU_(disambiguation)", result?.url)
        }
    }
}