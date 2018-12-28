package com.colacelli.ircbot.plugins.translate

import com.colacelli.ircbot.IRCBot
import com.colacelli.ircbot.base.Access
import com.colacelli.ircbot.base.listeners.OnChannelCommandListener
import com.colacelli.ircbot.plugins.translate.apertium.ApertiumTranslation
import com.colacelli.ircbot.plugins.translate.apertium.ApertiumTranslator
import com.colacelli.irclib.actors.Channel
import com.colacelli.irclib.actors.User
import com.colacelli.irclib.connection.Connection
import com.colacelli.irclib.messages.ChannelMessage
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.*
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class TranslatePluginTest {
    private val connection = mock<Connection> {}
    private val listeners = mock<ArrayList<OnChannelCommandListener>>()
    private val bot = mock<IRCBot> {
        on { connection } doReturn connection
        on { listeners } doReturn listeners
    }

    @UseExperimental(ObsoleteCoroutinesApi::class) private val mainDispatcher = newSingleThreadContext("TranslatePluginTest thread")
    private val plugin = TranslatePlugin()

    @BeforeEach fun initialize() {
        @UseExperimental(ExperimentalCoroutinesApi::class) Dispatchers.setMain(mainDispatcher)
    }

    @AfterEach fun finalize() {
        @UseExperimental(ExperimentalCoroutinesApi::class) Dispatchers.resetMain()
        mainDispatcher.close()
    }

    @Test
    fun getName() {
        assertEquals("translate", plugin.name)
    }

    @Test
    fun onLoad() {
        plugin.onLoad(bot)
        argumentCaptor<OnChannelCommandListener>().apply {
            verify(bot).addListener(capture())

            assertEquals(".translate", firstValue.command)
            assertEquals(Access.Level.USER, firstValue.level)
            assertEquals(".tra", firstValue.aliases!!.joinToString(""))
        }
    }

    @Test
    fun onUnload() {
        plugin.onUnload(bot)
        argumentCaptor<String>().apply {
            verify(bot).removeListener(capture())
            assertEquals(".translate", firstValue)
        }
    }

    @Test
    fun commands() {
        val translatorMock = mock<ApertiumTranslator> {
            on { dispatcher } doReturn mainDispatcher
            on { translate(any(), any(), any()) } doReturn GlobalScope.async { ApertiumTranslation("en", "es", "hello", "hola") }
        }
        val pluginMock = spy<TranslatePlugin> {
        }

        whenever(pluginMock.dispatcher).thenReturn(mainDispatcher)
        whenever(pluginMock.translator).thenReturn(translatorMock)

        lateinit var listener : OnChannelCommandListener

        val message = mock<ChannelMessage> {
            on { channel } doReturn Channel("#test")
            on { sender } doReturn User("r")
        }

        pluginMock.onLoad(bot)

        argumentCaptor<OnChannelCommandListener>().apply {
            verify(bot).addListener(capture())

            listener = firstValue
        }

        runBlocking(mainDispatcher) {
            listener.onChannelCommand(connection, message, ".translate", arrayOf("en", "es", "hello"))
        }

        verify(pluginMock.translator).translate("en", "es", "hello")
    }
}