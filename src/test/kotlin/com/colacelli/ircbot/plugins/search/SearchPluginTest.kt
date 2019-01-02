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
import kotlinx.coroutines.*
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll

internal class SearchPluginTest {
    private val connection = mock<Connection>()
    private val bot = mock<IRCBot> {
        on { connection } doReturn connection
    }

    private val plugin = SearchPlugin()

    companion object {
        private val mainDispatcher = newSingleThreadContext("SearchPluginTest thread")

        @BeforeAll
        @JvmStatic
        internal fun dispatcherSet() {
            @UseExperimental(ExperimentalCoroutinesApi::class) Dispatchers.setMain(mainDispatcher)
        }

        @AfterAll
        @JvmStatic
        internal fun dispatcherReset() {
            @UseExperimental(ExperimentalCoroutinesApi::class) Dispatchers.resetMain()
            mainDispatcher.close()
        }
    }

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
            verify(bot).removeListener(capture())
            assertEquals(".search", firstValue)
        }
    }

    @Test
    fun commands() {
        val searcherMock = mock<DuckDuckGoSearcher> {
            on { search(any()) } doReturn GlobalScope.async { DuckDuckGoSearchResult("GNU", "GNU is not Unix", "Wikipedia", "https://gnu.org") }
        }
        val pluginSpy = spy<SearchPlugin> {
            on { searcher } doReturn searcherMock
        }

        var listener : OnChannelCommandListener

        val message = mock<ChannelMessage> {
            on { channel } doReturn Channel("#test")
            on { sender } doReturn User("r")
        }

        pluginSpy.onLoad(bot)

        argumentCaptor<OnChannelCommandListener>().apply {
            verify(bot).addListener(capture())

            listener = firstValue
        }

        runBlocking {
            listener.onChannelCommand(connection, message, ".search", arrayOf("gnu"))
        }
        verify(searcherMock).search("gnu")
    }
}