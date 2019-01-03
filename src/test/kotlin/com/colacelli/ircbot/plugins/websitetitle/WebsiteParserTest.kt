package com.colacelli.ircbot.plugins.websitetitle

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class WebsiteParserTest {
    private val parser = WebsiteParser()

    @Test
    fun parseTitle() {
        runBlocking {
            val title = parser.parseTitle("https://gnu.org").await()
            assertEquals("The GNU Operating System and the Free Software Movement", title)
        }
    }
}