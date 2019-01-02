package com.colacelli.ircbot.plugins.access

import com.colacelli.ircbot.IRCBot
import com.colacelli.ircbot.base.Access
import com.colacelli.ircbot.base.listeners.OnChannelCommandListener
import com.colacelli.irclib.actors.Channel
import com.colacelli.irclib.actors.User
import com.colacelli.irclib.connection.Connection
import com.colacelli.irclib.messages.ChannelMessage
import com.nhaarman.mockitokotlin2.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class AccessPluginTest {
    private val access = mock<Access> {}
    private val connection = mock<Connection> {}
    private val listeners = mock<ArrayList<OnChannelCommandListener>>()
    private val bot = mock<IRCBot> {
        on { connection } doReturn connection
        on { listeners } doReturn listeners
        on { access } doReturn access
    }
    private val plugin = AccessPlugin()

    @Test
    fun getName() {
        assertEquals("access", plugin.name)
    }

    @Test
    fun onLoad() {
        plugin.onLoad(bot)
        argumentCaptor<OnChannelCommandListener>().apply {
            verify(bot, times(3)).addListener(capture())

            val addListener = allValues.first { it.command == ".accessAdd" }
            assertEquals(".accessAdd", addListener.command)
            assertEquals(Access.Level.ROOT, addListener.level)
            val addAliases = addListener.aliases!!
            addAliases.sort()
            assertEquals(".acc+ .accAdd", addAliases.joinToString(" "))

            val delListener = allValues.first { it.command == ".accessDel" }
            assertEquals(".accessDel", delListener.command)
            assertEquals(Access.Level.ROOT, delListener.level)
            val delAliases = delListener.aliases!!
            delAliases.sort()
            assertEquals(".acc- .accDel", delAliases.joinToString(" "))

            val listListener = allValues.first { it.command == ".accessList" }
            assertEquals(".accessList", listListener.command)
            assertEquals(Access.Level.OPERATOR, listListener.level)
            val listAliases = listListener.aliases!!
            listAliases.sort()
            assertEquals(".acc .accList", listAliases.joinToString(" "))
        }
    }

    @Test
    fun onUnload() {
        plugin.onUnload(bot)
        argumentCaptor<Array<String>>().apply {
            verify(bot).removeListenersByCommands(capture())
            val delListeners = firstValue
            delListeners.sort()
            assertEquals(".accessAdd .accessDel .accessList", delListeners.joinToString(" "))
        }
    }

    @Test
    fun commands() {
        lateinit var addListener: OnChannelCommandListener
        lateinit var delListener: OnChannelCommandListener
        lateinit var listListener: OnChannelCommandListener

        val message = mock<ChannelMessage> {
            on { channel } doReturn Channel("#test")
            on { sender } doReturn User("r")
        }

        plugin.onLoad(bot)
        argumentCaptor<OnChannelCommandListener>().apply {
            verify(bot, times(3)).addListener(capture())

            addListener = allValues.first { it.command == ".accessAdd" }
            delListener = allValues.first { it.command == ".accessDel" }
            listListener = allValues.first { it.command == ".accessList" }
        }

        addListener.onChannelCommand(bot.connection, message, ".accessAdd", arrayOf("t", "root"))
        verify(bot.access).add("t", Access.Level.ROOT)

        delListener.onChannelCommand(bot.connection, message, ".accessDel", arrayOf("t"))
        verify(bot.access).del("t")

        listListener.onChannelCommand(bot.connection, message, ".accessList", arrayOf())
        verify(bot.access).list()
    }
}