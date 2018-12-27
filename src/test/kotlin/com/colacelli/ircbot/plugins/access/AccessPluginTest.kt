package com.colacelli.ircbot.plugins.access

import com.colacelli.ircbot.IRCBot
import com.colacelli.ircbot.base.listeners.OnChannelCommandListener
import com.colacelli.irclib.connection.Connection
import com.nhaarman.mockitokotlin2.*
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class AccessPluginTest {
    private val connection = mock<Connection> {}
    private val listeners = mock<ArrayList<OnChannelCommandListener>>()
    private val bot = mock<IRCBot> {
        on { connection } doReturn connection
        on { listeners } doReturn listeners
    }
    private val accessPlugin = AccessPlugin()

    @Test
    fun getName() {
        assertEquals("access", accessPlugin.name)
    }

    @Test
    fun onLoad() {
        accessPlugin.onLoad(bot)
        argumentCaptor<OnChannelCommandListener>().apply {
            verify(bot, times(3)).addListener(capture())

            val addListener = allValues.first { it.command == ".accessAdd" }
            assertEquals(".accessAdd", addListener.command)
            val addAliases = addListener.aliases!!
            addAliases.sort()
            assertEquals(".acc+ .accAdd", addAliases.joinToString(" "))

            val delListener = allValues.first { it.command == ".accessDel" }
            assertEquals(".accessDel", delListener.command)
            val delAliases = delListener.aliases!!
            delAliases.sort()
            assertEquals(".acc- .accDel", delAliases.joinToString(" "))

            val listListener = allValues.first { it.command == ".accessList" }
            assertEquals(".accessList", listListener.command)
            val listAliases = listListener.aliases!!
            listAliases.sort()
            assertEquals(".acc .accList", listAliases.joinToString(" "))
        }
    }

    @Test
    fun onUnload() {
        accessPlugin.onUnload(bot)
        argumentCaptor<Array<String>>().apply {
            verify(bot).removeListeners(capture())
            val delListeners = firstValue
            delListeners.sort()
            assertEquals(".accessAdd .accessDel .accessList", delListeners.joinToString(" "))
        }
    }
}