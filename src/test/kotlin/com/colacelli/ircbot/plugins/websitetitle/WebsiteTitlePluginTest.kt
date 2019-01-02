package com.colacelli.ircbot.plugins.websitetitle

import com.colacelli.ircbot.IRCBot
import com.colacelli.irclib.actors.Channel
import com.colacelli.irclib.actors.User
import com.colacelli.irclib.connection.Connection
import com.colacelli.irclib.connection.listeners.OnChannelMessageListener
import com.colacelli.irclib.messages.ChannelMessage
import com.nhaarman.mockitokotlin2.*
import jdk.nashorn.internal.ir.annotations.Ignore
import kotlinx.coroutines.*
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import java.util.concurrent.Executors

internal class WebsiteTitlePluginTest {
    private val connection = mock<Connection>()
    private val bot = mock<IRCBot> {
        on { connection } doReturn connection
    }

    private val plugin = WebsiteTitlePlugin()

    companion object {
        private val mainDispatcher = newSingleThreadContext("WebsiteTitlePluginTest thread")

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

    @Test fun getName() {
        assertEquals("website_title", plugin.name)
    }

    @Test fun onLoad() {
        plugin.onLoad(bot)
        verify(bot).addListener(any<OnChannelMessageListener>())
    }

    @Test fun onUnload() {
        plugin.onUnload(bot)
        verify(bot).removeListener(any<OnChannelMessageListener>())
    }

    @Test fun behavior() {
        val parserMock = mock<WebsiteParser> {
            on { parseTitle(any()) } doReturn GlobalScope.async(mainDispatcher) { "Title test" }
        }
        val pluginMock = spy<WebsiteTitlePlugin> {
            on { parser } doReturn parserMock
        }

        var listener : OnChannelMessageListener

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