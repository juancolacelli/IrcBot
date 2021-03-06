package com.colacelli.ircbot.plugins.search

import com.colacelli.ircbot.IRCBot
import com.colacelli.ircbot.base.Access
import com.colacelli.ircbot.base.listeners.OnChannelCommandListener
import com.colacelli.ircbot.plugins.search.duckduckgo.DuckDuckGoSearchResult
import com.colacelli.ircbot.plugins.search.duckduckgo.DuckDuckGoSearcher
import com.colacelli.irclib.actors.Channel
import com.colacelli.irclib.actors.User
import com.colacelli.irclib.connection.Connection
import com.colacelli.irclib.messages.ChannelMessage
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class SearchPluginTest {
    private val connection = mock<Connection>()
    private val bot = mock<IRCBot> {
        on { connection } doReturn connection
    }

    private val plugin = SearchPlugin()

    @Test
    fun getName() {
        assertEquals("search", plugin.name)
    }

    @Test
    fun onLoad() {
        plugin.onLoad(bot)
        argumentCaptor<OnChannelCommandListener>().apply {
            verify(bot).addListener(capture())

            assertEquals(".search", firstValue.command)
            assertEquals(Access.Level.USER, firstValue.level)
            assertEquals(".ddgo", firstValue.aliases!!.joinToString(""))
        }
    }

    @Test
    fun onUnload() {
        plugin.onUnload(bot)
        argumentCaptor<String>().apply {
            verify(bot).removeListenerByCommand(capture())
            assertEquals(".search", firstValue)
        }
    }

    @Test
    fun commands() = runBlocking {
        val searcherMock = mock<DuckDuckGoSearcher> {
            on { search(any()) } doReturn GlobalScope.async { DuckDuckGoSearchResult("GNU", "GNU is not UNIX", "Wikipedia", "https://gnu.org") }
        }
        val pluginSpy = spy<SearchPlugin> {
            on { searcher } doReturn searcherMock
        }

        var listener: OnChannelCommandListener

        val message = mock<ChannelMessage> {
            on { channel } doReturn Channel("#test")
            on { sender } doReturn User("r")
        }

        pluginSpy.onLoad(bot)

        argumentCaptor<OnChannelCommandListener>().apply {
            verify(bot).addListener(capture())

            listener = firstValue
        }

        listener.onChannelCommand(connection, message, ".search", arrayOf("gnu"))
        verify(searcherMock).search("gnu")
    }
}