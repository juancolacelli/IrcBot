package com.colacelli.ircbot.plugins.rssfeed

import com.colacelli.ircbot.IRCBot
import com.colacelli.ircbot.base.Access
import com.colacelli.ircbot.base.listeners.OnChannelCommandListener
import com.colacelli.irclib.actors.Channel
import com.colacelli.irclib.actors.User
import com.colacelli.irclib.connection.Connection
import com.colacelli.irclib.connection.listeners.OnPingListener
import com.colacelli.irclib.messages.ChannelMessage
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class RSSFeedPluginTest {
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
    private val plugin = RSSFeedPlugin()

    @Test
    fun getName() {
        assertEquals("rss_feed", plugin.name)
    }

    @Test
    fun onLoad() {
        plugin.onLoad(bot)
        verify(bot).addListener(any<OnPingListener>())
        argumentCaptor<OnChannelCommandListener>().apply {
            verify(bot, times(7)).addListener(capture())

            assertEquals(".rssSubscribe", allValues[0].command)
            assertEquals(Access.Level.USER, allValues[0].level)
            assertEquals(".rssSub", allValues[0].aliases!!.joinToString(""))

            assertEquals(".rssUnsubscribe", allValues[1].command)
            assertEquals(Access.Level.USER, allValues[1].level)
            assertEquals(".rssUnsub", allValues[1].aliases!!.joinToString(""))

            assertEquals(".rssAdd", allValues[2].command)
            assertEquals(Access.Level.ADMIN, allValues[2].level)
            assertEquals(".rss+", allValues[2].aliases!!.joinToString(""))

            assertEquals(".rssDel", allValues[3].command)
            assertEquals(Access.Level.ADMIN, allValues[3].level)
            assertEquals(".rss-", allValues[3].aliases!!.joinToString(""))

            assertEquals(".rssList", allValues[4].command)
            assertEquals(Access.Level.USER, allValues[4].level)
            assertEquals(".rss", allValues[4].aliases!!.joinToString(""))

            assertEquals(".rssCheck", allValues[5].command)
            assertEquals(Access.Level.OPERATOR, allValues[5].level)
            assertEquals(".rss()", allValues[5].aliases!!.joinToString(""))

            assertEquals(".rssSubscribers", allValues[6].command)
            assertEquals(Access.Level.OPERATOR, allValues[6].level)
            assertNull(allValues[6].aliases)
        }
    }

    @Test
    fun onUnload() {
        plugin.onUnload(bot)
        verify(bot).removeListener(any<OnPingListener>())
        argumentCaptor<Array<String>>().apply {
            verify(bot).removeListenersByCommands(capture())

            assertNotEquals(-1, firstValue.indexOf(".rssSubscribe"))
            assertNotEquals(-1, firstValue.indexOf(".rssUnsubscribe"))
            assertNotEquals(-1, firstValue.indexOf(".rssAdd"))
            assertNotEquals(-1, firstValue.indexOf(".rssDel"))
            assertNotEquals(-1, firstValue.indexOf(".rssList"))
            assertNotEquals(-1, firstValue.indexOf(".rssCheck"))
            assertNotEquals(-1, firstValue.indexOf(".rssSubscribers"))
        }
    }

    @Test
    fun behavior() {
        var listener: OnPingListener

        val rssFeedMock = mock<RSSFeed> {
            on { check() } doReturn mock()
        }
        val pluginSpy = spy<RSSFeedPlugin> {
            on { rssFeed } doReturn rssFeedMock
        }
        pluginSpy.onLoad(bot)
        argumentCaptor<OnPingListener>().apply {
            verify(bot).addListener(capture())
            listener = firstValue
        }

        runBlocking {
            listener.onPing(connection)
            verify(pluginSpy.rssFeed).check()
        }
    }

    @Test
    fun commands() {
        var listeners: List<OnChannelCommandListener>

        val rssFeedMock = mock<RSSFeed> {
            on { check() } doReturn mock()
        }
        val pluginSpy = spy<RSSFeedPlugin> {
            on { rssFeed } doReturn rssFeedMock
        }
        pluginSpy.onLoad(bot)
        argumentCaptor<OnChannelCommandListener>().apply {
            verify(bot, times(7)).addListener(capture())
            listeners = allValues
        }

        assertEquals(".rssSubscribe", listeners[0].command)
        val user = User("nick")
        val message = ChannelMessage(Channel("#test"), ".test", user)
        listeners[0].onChannelCommand(connection, message, ".rssSubscribe", arrayOf())
        verify(rssFeedMock).subscribe(user)

        assertEquals(".rssUnsubscribe", listeners[1].command)
        listeners[1].onChannelCommand(connection, message, ".rssUnsubscribe", arrayOf())
        verify(rssFeedMock).unsubscribe(user)

        val url = "https://test.org/rss"
        assertEquals(".rssAdd", listeners[2].command)
        listeners[2].onChannelCommand(connection, message, ".rssAdd", arrayOf(url))
        verify(rssFeedMock).add(url)

        assertEquals(".rssDel", listeners[3].command)
        listeners[3].onChannelCommand(connection, message, ".rssDel", arrayOf(url))
        verify(rssFeedMock).del(url)

        assertEquals(".rssList", listeners[4].command)
        listeners[4].onChannelCommand(connection, message, ".rssList", arrayOf())
        verify(rssFeedMock).list()

        runBlocking {
            assertEquals(".rssCheck", listeners[5].command)
            listeners[5].onChannelCommand(connection, message, ".rssCheck", arrayOf())
            verify(rssFeedMock).check()
        }

        assertEquals(".rssSubscribers", listeners[6].command)
        listeners[6].onChannelCommand(connection, message, ".rssSubscribers", arrayOf())
        verify(rssFeedMock).subscribers()
    }
}