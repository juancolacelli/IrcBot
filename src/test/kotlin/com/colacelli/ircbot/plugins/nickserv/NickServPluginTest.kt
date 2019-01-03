package com.colacelli.ircbot.plugins.nickserv

import com.colacelli.ircbot.IRCBot
import com.colacelli.irclib.connection.Connection
import com.colacelli.irclib.connection.listeners.OnConnectListener
import com.colacelli.irclib.messages.PrivateMessage
import com.nhaarman.mockitokotlin2.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class NickServPluginTest {
    private val plugin = NickServPlugin("test")
    private val connection = mock<Connection> {}
    private val bot = mock<IRCBot> {
        on { connection } doReturn connection
    }

    @Test
    fun getName() {
        assertEquals("nickserv", plugin.name)
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

        argumentCaptor<PrivateMessage>().apply {
            verify(connection).send(capture())
            assertEquals("identify test", firstValue.text)
            assertEquals("NickServ", firstValue.receiver!!.nick)
        }
    }
}