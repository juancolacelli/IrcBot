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
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class TranslatePluginTest {
    private val connection = mock<Connection>()
    private val bot = mock<IRCBot> {
        on { connection } doReturn connection
    }

    private val plugin = TranslatePlugin()

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
            verify(bot).removeListenerByCommand(capture())
            assertEquals(".translate", firstValue)
        }
    }

    @Test
    fun commands() {
        val translatorMock = mock<ApertiumTranslator> {
            on { translate(any(), any(), any()) } doReturn GlobalScope.async { ApertiumTranslation("en", "es", "hello", "hola") }
        }
        val pluginSpy = spy<TranslatePlugin> {
            on { translator } doReturn translatorMock
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

        runBlocking {
            listener.onChannelCommand(connection, message, ".translate", arrayOf("en", "es", "hello"))
        }
        verify(translatorMock).translate("en", "es", "hello")
    }
}