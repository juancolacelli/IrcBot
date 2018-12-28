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
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach

internal class TranslatePluginTest {
    private val connection = mock<Connection> {}
    private val listeners = mock<ArrayList<OnChannelCommandListener>>()
    private val bot = mock<IRCBot> {
        on { connection } doReturn connection
        on { listeners } doReturn listeners
    }
    private val translatePlugin = TranslatePlugin()
    private val dispatcher = newSingleThreadContext("UI thread")

    @BeforeEach fun initialize() {
        Dispatchers.setMain(dispatcher)
    }

    @AfterEach fun finalize() {
        Dispatchers.resetMain()
        dispatcher.close()
    }

    @Test
    fun getName() {
        assertEquals("translate", translatePlugin.name)
    }

    @Test
    fun onLoad() {
        translatePlugin.onLoad(bot)
        argumentCaptor<OnChannelCommandListener>().apply {
            verify(bot).addListener(capture())

            assertEquals(".translate", firstValue.command)
            assertEquals(Access.Level.USER, firstValue.level)
            assertEquals(".tra", firstValue.aliases!!.joinToString(""))
        }
    }

    @Test
    fun onUnload() {
        translatePlugin.onUnload(bot)
        argumentCaptor<String>().apply {
            verify(bot).removeListener(capture())
            assertEquals(".translate", firstValue)
        }
    }

    @Test
    fun commands() {
        lateinit var listener : OnChannelCommandListener

        val message = mock<ChannelMessage> {
            on { channel } doReturn Channel("#test")
            on { sender } doReturn User("r")
        }

        val translatorMock = mock<ApertiumTranslator> {
            on { translate("en", "es", "hello") } doReturn GlobalScope.async(dispatcher) { ApertiumTranslation("en", "es", "hello", "hola") }
        }

        val translatePluginMock = spy<TranslatePlugin> {
            on { translator } doReturn translatorMock
            on { dispatcher } doReturn dispatcher
        }

        translatePluginMock.onLoad(bot)

        argumentCaptor<OnChannelCommandListener>().apply {
            verify(bot).addListener(capture())

            listener = firstValue
        }

        runBlocking(dispatcher) {
            listener.onChannelCommand(connection, message, ".translate", arrayOf("en", "es", "hello"))
        }

        verify(translatorMock).translate("en", "es", "hello")
    }
}