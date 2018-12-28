package com.colacelli.ircbot.plugins.autoop

import com.colacelli.ircbot.IRCBot
import com.colacelli.irclib.actors.Channel
import com.colacelli.irclib.actors.User
import com.colacelli.irclib.connection.Connection
import com.colacelli.irclib.connection.listeners.Listener
import com.colacelli.irclib.connection.listeners.OnChannelModeListener
import com.colacelli.irclib.connection.listeners.OnJoinListener
import com.colacelli.irclib.messages.PrivateMessage
import com.nhaarman.mockitokotlin2.*
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class AutoOpPluginTest {
    private val user = User("test")
    private val plugin = spy<AutoOpPlugin>()
    private val connection = mock<Connection> {
        on { user } doReturn user
    }
    private val bot = mock<IRCBot> {
        on { connection } doReturn connection
    }

    @Test
    fun getName() {
        assertEquals("auto_op", plugin.name)
    }

    @Test
    fun onLoad() {
        plugin.onLoad(bot)
        verify(bot).addListener(any<OnJoinListener>())
        verify(bot).addListener(any<OnChannelModeListener>())
    }

    @Test
    fun onUnload() {
        plugin.onUnload(bot)
        verify(bot).removeListener(any<OnJoinListener>())
        verify(bot).removeListener(any<OnChannelModeListener>())
    }

    @Test
    fun claimOp() {
        val channel = Channel("#test")
        plugin.claimOp(connection, channel)
        argumentCaptor<PrivateMessage>().apply {
            verify(connection).send(capture())
            assertEquals("ChanServ", firstValue.receiver!!.nick)
            assertEquals("op #test", firstValue.text)
        }
    }

    @Test
    fun behavior() {
        val channel = Channel("#test")
        var joinListener : OnJoinListener
        var channelModeListener : OnChannelModeListener

        plugin.onLoad(bot)
        argumentCaptor<Listener>().apply {
            verify(bot, times(2)).addListener(capture())
            joinListener = firstValue as OnJoinListener
            channelModeListener = secondValue as OnChannelModeListener
        }
        joinListener.onJoin(connection, connection.user, channel)
        channelModeListener.onChannelMode(connection, channel, "-o", "test")
        verify(plugin, times(2)).claimOp(connection, channel)
    }
}