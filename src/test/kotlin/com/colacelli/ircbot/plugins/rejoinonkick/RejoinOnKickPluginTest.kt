package com.colacelli.ircbot.plugins.rejoinonkick

import com.colacelli.ircbot.IRCBot
import com.colacelli.irclib.actors.Channel
import com.colacelli.irclib.actors.User
import com.colacelli.irclib.connection.Connection
import com.colacelli.irclib.connection.Rawable
import com.colacelli.irclib.connection.listeners.Listener
import com.colacelli.irclib.connection.listeners.OnKickListener
import com.colacelli.irclib.connection.listeners.OnRawCodeListener
import com.colacelli.irclib.messages.PrivateMessage
import com.nhaarman.mockitokotlin2.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class RejoinOnKickPluginTest {
    private val connection = mock<Connection> {
        on { user } doReturn User("test")
    }
    private val bot = mock<IRCBot> {
        on { connection } doReturn connection
    }

    private val plugin = RejoinOnKickPlugin()

    @Test
    fun getName() {
        assertEquals("rejoin_on_kick", plugin.name)
    }

    @Test
    fun onLoad() {
        plugin.onLoad(bot)
        verify(bot).addListener(any<OnKickListener>())
        verify(bot).addListener(any<OnRawCodeListener>())
    }

    @Test
    fun onUnload() {
        plugin.onUnload(bot)
        verify(bot).removeListener(any<OnKickListener>())
        verify(bot).removeListener(any<OnRawCodeListener>())
    }

    @Test
    fun behavior() {
        plugin.onLoad(bot)
        val kickListener: Listener
        val banListener: Listener
        argumentCaptor<Listener>().apply {
            verify(bot, times(2)).addListener(capture())
            kickListener = firstValue
            banListener = secondValue
        }

        assert(kickListener is OnKickListener)
        if (kickListener is OnKickListener) kickListener.onKick(connection, connection.user, Channel("#test"))
        argumentCaptor<Channel>().apply {
            verify(connection).join(capture())
            assertEquals("#test", firstValue.name)
        }

        assert(banListener is OnRawCodeListener)
        if (banListener is OnRawCodeListener) banListener.onRawCode(connection, "Banned", Rawable.RawCode.JOIN_BANNED.code, listOf("", "", "", "#test"))
        argumentCaptor<PrivateMessage>().apply {
            verify(connection, timeout(6000)).send(capture())
            assertEquals("unban #test", firstValue.text)
            assertEquals("ChanServ", firstValue.receiver!!.nick)
        }
        argumentCaptor<Channel>().apply {
            verify(connection, times(2)).join(capture())
            assertEquals("#test", firstValue.name)
        }
    }
}