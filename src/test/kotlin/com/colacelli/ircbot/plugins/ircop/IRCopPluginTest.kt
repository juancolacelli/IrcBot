package com.colacelli.ircbot.plugins.ircop

import com.colacelli.ircbot.IRCBot
import com.colacelli.ircbot.base.Access
import com.colacelli.ircbot.base.listeners.OnChannelCommandListener
import com.colacelli.irclib.actors.Channel
import com.colacelli.irclib.actors.User
import com.colacelli.irclib.connection.Connection
import com.colacelli.irclib.connection.listeners.OnConnectListener
import com.colacelli.irclib.messages.ChannelMessage
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class IRCopPluginTest {
    private val connection = mock<Connection> {
        on { user } doReturn User("test")
    }
    private val access = mock<Access> {
        on { get(any()) } doReturn Access.Level.ADMIN
    }
    private val bot = mock<IRCBot> {
        on { connection } doReturn connection
        on { access } doReturn access
    }
    private val plugin = IRCopPlugin("name", "p455w0rd")

    @Test
    fun getName() {
        assertEquals("ircop", plugin.name)
    }

    @Test
    fun onLoad() {
        plugin.onLoad(bot)
        verify(bot).addListener(any<OnConnectListener>())
        argumentCaptor<OnChannelCommandListener>().apply {
            verify(bot).addListener(capture())

            assertEquals(".kill", firstValue.command)
            assertEquals(Access.Level.ADMIN, firstValue.level)
            assertNull(firstValue.aliases)
        }
    }

    @Test
    fun onUnload() {
        plugin.onUnload(bot)
        verify(bot).removeListener(any<OnConnectListener>())
        argumentCaptor<String>().apply {
            verify(bot).removeListener(capture())
            assertEquals(".kill", firstValue)
        }
    }

    @Test
    fun behavior() {
        plugin.onLoad(bot)
        argumentCaptor<OnConnectListener>().apply {
            verify(bot).addListener(capture())
            firstValue.onConnect(connection, mock(), mock())
        }

        argumentCaptor<String>().apply {
            verify(connection).send(capture())
            assertEquals("OPER name p455w0rd", firstValue)
        }
    }

    @Test
    fun commands() {
        var listener : OnChannelCommandListener

        val message = mock<ChannelMessage> {
            on { channel } doReturn Channel("#test")
            on { sender } doReturn User("r")
        }

        plugin.onLoad(bot)
        argumentCaptor<OnChannelCommandListener>().apply {
            verify(bot).addListener(capture())

            listener = firstValue
        }

        listener.onChannelCommand(connection, message, ".kill", arrayOf("test", "you", "must", "die"))
        listener.onChannelCommand(connection, message, ".kill", arrayOf("user", "you", "must", "die"))

        argumentCaptor<User>().apply {
            verify(bot.connection).kill(capture(), any())
            assertEquals("user", firstValue.nick)
        }

        argumentCaptor<String>().apply {
            verify(bot.connection).kill(any(), capture())
            assertEquals("you must die", firstValue)
        }
    }
}