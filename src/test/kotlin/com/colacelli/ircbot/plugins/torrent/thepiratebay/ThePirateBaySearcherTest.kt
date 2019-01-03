package com.colacelli.ircbot.plugins.torrent.thepiratebay

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

internal class ThePirateBaySearcherTest {
    private val searcher = ThePirateBaySearcher()

    @Test
    fun search() = runBlocking {
        withTimeout(10000L) {
            val result = searcher.search("stallman").await()
            assertNotNull(result)
            assertEquals(result?.title, "Richard Stallman's Lecture, WebTech-Sofia April 2005")
        }
    }
}