package com.colacelli.ircbot.plugins.torrent

import com.colacelli.ircbot.IRCBot
import com.colacelli.ircbot.base.Access
import com.colacelli.ircbot.base.listeners.OnChannelCommandListener
import com.colacelli.ircbot.plugins.torrent.thepiratebay.ThePirateBaySearchResult
import com.colacelli.ircbot.plugins.torrent.thepiratebay.ThePirateBaySearcher
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
            verify(bot).removeListenerByCommand(capture())
            assertEquals(".torrent", firstValue)
        }
    }

    @Test
    fun commands() {
        val searcherMock = mock<ThePirateBaySearcher> {
            on { search(any()) } doReturn GlobalScope.async { mock<ThePirateBaySearchResult>() }
        }
        val pluginSpy = spy<TorrentPlugin> {
            on { searcher } doReturn searcherMock
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
            listener.onChannelCommand(connection, message, ".torrent", arrayOf("gnu"))
        }
        verify(searcherMock).search("gnu")
    }
}