package com.colacelli.ircbot.plugins.joinpart

import com.colacelli.ircbot.IRCBot
import com.colacelli.ircbot.base.Access
import com.colacelli.ircbot.base.listeners.OnChannelCommandListener
import com.colacelli.irclib.actors.Channel
import com.colacelli.irclib.actors.User
import com.colacelli.irclib.connection.Connection
import com.colacelli.irclib.connection.listeners.OnConnectListener
import com.colacelli.irclib.messages.ChannelMessage
import com.nhaarman.mockitokotlin2.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

internal class JoinPartPluginTest {
    private val connection = mock<Connection> {
        on { user } doReturn User("test")
    }
    private val access = mock<Access> {
        on { get(any()) } doReturn Access.Level.OPERATOR
    }
    private val bot = mock<IRCBot> {
        on { connection } doReturn connection
        on { access } doReturn access
    }
    private val plugin = JoinPartPlugin()
    private val manager = mock<ChannelsManager> {
        on { list() } doReturn arrayListOf("#channel_a", "#channel_b")
    }

    @Test
    fun getName() {
        assertEquals("join_part", plugin.name)
    }

    @Test
    fun onLoad() {
        plugin.onLoad(bot)
        argumentCaptor<OnChannelCommandListener>().apply {
            verify(bot, times(2)).addListener(capture())

            assertEquals(".join", firstValue.command)
            assertEquals(Access.Level.OPERATOR, firstValue.level)
            assertNull(firstValue.aliases)

            assertEquals(".part", secondValue.command)
            assertEquals(Access.Level.OPERATOR, secondValue.level)
            assertNull(secondValue.aliases)
        }
    }

    @Test
    fun onUnload() {
        plugin.onUnload(bot)
        argumentCaptor<Array<String>>().apply {
            verify(bot).removeListenersByCommands(capture())
            assertEquals(".join", firstValue[0])
            assertEquals(".part", firstValue[1])
        }
    }

    @Test
    fun commands() {
        var joinListener: OnChannelCommandListener
        var partListener: OnChannelCommandListener

        val message = mock<ChannelMessage> {
            on { channel } doReturn Channel("#test")
            on { sender } doReturn User("r")
        }

        val pluginSpy = spy<JoinPartPlugin> {
            on { manager } doReturn manager
        }
        pluginSpy.onLoad(bot)
        argumentCaptor<OnChannelCommandListener>().apply {
            verify(bot, times(2)).addListener(capture())

            joinListener = firstValue
            partListener = secondValue
        }

        joinListener.onChannelCommand(connection, message, ".join", arrayOf("#test"))
        argumentCaptor<Channel>().apply {
            verify(bot.connection).join(capture())
            assertEquals("#test", firstValue.name)
            verify(manager).add("#test")
        }

        partListener.onChannelCommand(connection, message, ".part", arrayOf("#test"))
        argumentCaptor<Channel>().apply {
            verify(bot.connection).part(capture())
            assertEquals("#test", firstValue.name)
            verify(manager).del("#test")
        }
    }

    @Test
    fun behavior() {
        val pluginSpy = spy<JoinPartPlugin> {
            on { manager } doReturn manager
        }
        pluginSpy.onLoad(bot)
        argumentCaptor<OnConnectListener>().apply {
            verify(bot).addListener(capture())
            firstValue.onConnect(connection, mock(), mock())
        }

        argumentCaptor<Channel>().apply {
            verify(connection, times(2)).join(capture())
            assertEquals("#channel_a", firstValue.name)
            assertEquals("#channel_b", secondValue.name)
        }
    }
}