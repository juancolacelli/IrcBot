package com.colacelli.ircbot.plugins.websitetitle

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class WebsiteParserTest {
    private val parser = WebsiteParser()

    @Test
    fun parseTitle() = runBlocking {
        withTimeout(10000L) {
            val title = parser.parseTitle("https://gnu.org").await()
            assertEquals("The GNU Operating System and the Free Software Movement", title)
        }
    }
}