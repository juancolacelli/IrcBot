package com.colacelli.ircbot.plugins.websitetitle

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.jsoup.nodes.Document
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

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