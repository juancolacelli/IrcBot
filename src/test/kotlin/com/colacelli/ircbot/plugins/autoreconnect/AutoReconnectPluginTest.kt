package com.colacelli.ircbot.plugins.autoreconnect

import com.colacelli.ircbot.IRCBot
import com.colacelli.ircbot.base.listeners.OnChannelCommandListener
import com.colacelli.irclib.connection.Connection
import com.colacelli.irclib.connection.listeners.OnDisconnectListener
import com.nhaarman.mockitokotlin2.*
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class AutoReconnectPluginTest {
    private val plugin = AutoReconnectPlugin()
    private val connection = mock<Connection> {}
    private val bot = mock<IRCBot> {
        on { connection } doReturn connection
    }

    @Test
    fun getName() {
        assertEquals("auto_reconnect", plugin.name)
    }

    @Test
    fun onLoad() {
        plugin.onLoad(bot)
        verify(bot).addListener(any<OnDisconnectListener>())
    }

    @Test
    fun onUnload() {
        plugin.onUnload(bot)
        verify(bot).removeListener(any<OnDisconnectListener>())
    }

    @Test
    fun behavior() {
        plugin.onLoad(bot)
        argumentCaptor<OnDisconnectListener>().apply {
            verify(bot).addListener(capture())
            firstValue.onDisconnect(connection, mock())
        }

        verify(connection).connect()
    }
}
