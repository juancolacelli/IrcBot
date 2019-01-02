package com.colacelli.ircbot.plugins.torrent

import com.colacelli.ircbot.IRCBot
import com.colacelli.ircbot.base.Access
import com.colacelli.ircbot.base.listeners.OnChannelCommandListener
import com.colacelli.irclib.actors.User
import com.colacelli.irclib.connection.Connection
import com.nhaarman.mockitokotlin2.*
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class TorrentPluginTest {
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
    private val plugin = TorrentPlugin()

    @Test
    fun getName() {
        assertEquals("torrent", plugin.name)
    }

    @Test
    fun onLoad() {
        plugin.onLoad(bot)
        argumentCaptor<OnChannelCommandListener>().apply {
            verify(bot).addListener(capture())

            assertEquals(".torrent", firstValue.command)
            assertEquals(Access.Level.USER, firstValue.level)
            assertEquals(".tpb", firstValue.aliases!!.joinToString(""))
        }
    }

    @Test
    fun onUnload() {
        plugin.onUnload(bot)
        argumentCaptor<String>().apply {
            verify(bot).removeListener(capture())
            assertEquals(".torrent", firstValue)
        }
    }
}