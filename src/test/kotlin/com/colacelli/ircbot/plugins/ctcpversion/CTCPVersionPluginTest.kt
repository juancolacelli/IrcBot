package com.colacelli.ircbot.plugins.ctcpversion

import com.colacelli.ircbot.IRCBot
import com.colacelli.irclib.actors.Channel
import com.colacelli.irclib.actors.User
import com.colacelli.irclib.connection.Connection
import com.colacelli.irclib.connection.listeners.OnCTCPListener
import com.colacelli.irclib.messages.CTCPMessage
import com.nhaarman.mockitokotlin2.*
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class CTCPVersionPluginTest {
    private val plugin = CTCPVersionPlugin("test")
    private val connection = mock<Connection> {}
    private val bot = mock<IRCBot> {
        on { connection } doReturn connection
    }

    @Test
    fun getName() {
        assertEquals("ctcp_version", plugin.name)
    }

    @Test
    fun onLoad() {
        plugin.onLoad(bot)
        verify(bot).addListener(any<OnCTCPListener>())
    }

    @Test
    fun onUnload() {
        plugin.onUnload(bot)
        verify(bot).removeListener(any<OnCTCPListener>())
    }

    @Test
    fun behavior() {
        var listener : OnCTCPListener
        plugin.onLoad(bot)
        argumentCaptor<OnCTCPListener>().apply {
            verify(bot).addListener(capture())
            listener = firstValue
        }

        listener.onCTCP(connection, CTCPMessage("VERSION", "VERSION", User("test"), User("test")), mock())

        argumentCaptor<CTCPMessage>().apply {
            verify(connection).send(capture())
            assertEquals("VERSION", firstValue.command)
        }
    }
}