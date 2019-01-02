package com.colacelli.ircbot.plugins.help

import com.colacelli.ircbot.IRCBot
import com.colacelli.ircbot.base.Access
import com.colacelli.ircbot.base.Helper
import com.colacelli.ircbot.base.listeners.OnChannelCommandListener
import com.colacelli.irclib.actors.Channel
import com.colacelli.irclib.actors.User
import com.colacelli.irclib.connection.Connection
import com.colacelli.irclib.messages.ChannelMessage
import com.nhaarman.mockitokotlin2.*
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class HelpPluginTest {
    private val connection = mock<Connection>()
    private val helper = mock<Helper>()
    private val access = mock<Access> {
        on { get(any()) } doReturn Access.Level.USER
    }
    private val bot = mock<IRCBot> {
        on { connection } doReturn connection
        on { helper } doReturn helper
        on { access } doReturn access
    }

    private val plugin = HelpPlugin()

    @Test
    fun getName() {
        assertEquals("help", plugin.name)
    }

    @Test
    fun onLoad() {
        plugin.onLoad(bot)
        argumentCaptor<OnChannelCommandListener>().apply {
            verify(bot).addListener(capture())

            assertEquals(".help", firstValue.command)
            assertEquals(Access.Level.USER, firstValue.level)
            assertEquals(".?", firstValue.aliases!!.joinToString(""))
        }
    }

    @Test
    fun onUnload() {
        plugin.onUnload(bot)
        argumentCaptor<String>().apply {
            verify(bot).removeListenerByCommand(capture())
            assertEquals(".help", firstValue)
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

        listener.onChannelCommand(connection, message, ".help", arrayOf(""))
        verify(bot.helper).list(Access.Level.USER, "")
    }
}