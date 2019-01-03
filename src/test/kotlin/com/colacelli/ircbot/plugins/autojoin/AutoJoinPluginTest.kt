package com.colacelli.ircbot.plugins.autojoin

import com.colacelli.ircbot.IRCBot
import com.colacelli.irclib.actors.Channel
import com.colacelli.irclib.connection.Connection
import com.colacelli.irclib.connection.listeners.OnConnectListener
import com.nhaarman.mockitokotlin2.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class AutoJoinPluginTest {
    private val plugin = AutoJoinPlugin(arrayOf(Channel("#test"), Channel("#test2")))
    private val connection = mock<Connection> {}
    private val bot = mock<IRCBot> {
        on { connection } doReturn connection
    }

    @Test
    fun getName() {
        assertEquals("auto_join", plugin.name)
    }

    @Test
    fun onLoad() {
        plugin.onLoad(bot)
        verify(bot).addListener(any<OnConnectListener>())
    }

    @Test
    fun onUnload() {
        plugin.onUnload(bot)
        verify(bot).removeListener(any<OnConnectListener>())
    }

    @Test
    fun behavior() {
        plugin.onLoad(bot)
        argumentCaptor<OnConnectListener>().apply {
            verify(bot).addListener(capture())
            firstValue.onConnect(connection, mock(), mock())
        }

        argumentCaptor<Channel>().apply {
            verify(connection, times(2)).join(capture())
            assertEquals("#test", firstValue.name)
            assertEquals("#test2", secondValue.name)
        }
    }
}