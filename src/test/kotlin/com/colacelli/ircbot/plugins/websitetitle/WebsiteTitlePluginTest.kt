package com.colacelli.ircbot.plugins.websitetitle

import com.colacelli.ircbot.IRCBot
import com.colacelli.irclib.actors.Channel
import com.colacelli.irclib.actors.User
import com.colacelli.irclib.connection.Connection
import com.colacelli.irclib.connection.listeners.OnChannelMessageListener
import com.colacelli.irclib.messages.ChannelMessage
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class WebsiteTitlePluginTest {
    private val connection = mock<Connection>()
    private val bot = mock<IRCBot> {
        on { connection } doReturn connection
    }

    private val plugin = WebsiteTitlePlugin()

    @Test
    fun getName() {
        assertEquals("website_title", plugin.name)
    }

    @Test
    fun onLoad() {
        plugin.onLoad(bot)
        verify(bot).addListener(any<OnChannelMessageListener>())
    }

    @Test
    fun onUnload() {
        plugin.onUnload(bot)
        verify(bot).removeListener(any<OnChannelMessageListener>())
    }

    @Test
    fun behavior() {
        val parserMock = mock<WebsiteParser> {
            on { parseTitle(any()) } doReturn GlobalScope.async { "Title test" }
        }
        val pluginMock = spy<WebsiteTitlePlugin> {
            on { parser } doReturn parserMock
        }

        var listener: OnChannelMessageListener

        var message = mock<ChannelMessage> {
            on { sender } doReturn User("test")
            on { channel } doReturn Channel("#test")
            on { text } doReturn "GNU website is https://gnu.org"
        }

        pluginMock.onLoad(bot)

        argumentCaptor<OnChannelMessageListener>().apply {
            verify(bot).addListener(capture())

            listener = firstValue
        }


        runBlocking {
            listener.onChannelMessage(connection, message)
        }
        verify(pluginMock.parser).parseTitle("https://gnu.org")
    }
}