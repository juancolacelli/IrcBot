package com.colacelli.ircbot.plugins.uptime

import com.colacelli.ircbot.IRCBot
import com.colacelli.ircbot.base.Access
import com.colacelli.ircbot.base.listeners.OnChannelCommandListener
import com.colacelli.irclib.actors.Channel
import com.colacelli.irclib.actors.User
import com.colacelli.irclib.connection.Connection
import com.colacelli.irclib.messages.ChannelMessage
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class UptimePluginTest {
    private val connection = mock<Connection>()
    private val bot = mock<IRCBot> {
        on { connection } doReturn connection
    }

    private val plugin = UptimePlugin()

    @Test
    fun getName() {
        assertEquals("uptime", plugin.name)
    }

    @Test
    fun onLoad() {
        plugin.onLoad(bot)
        argumentCaptor<OnChannelCommandListener>().apply {
            verify(bot).addListener(capture())

            assertEquals(".uptime", firstValue.command)
            assertEquals(Access.Level.USER, firstValue.level)
            assertEquals(".up", firstValue.aliases!!.joinToString(""))
        }
    }

    @Test
    fun onUnload() {
        plugin.onUnload(bot)
        argumentCaptor<String>().apply {
            verify(bot).removeListenerByCommand(capture())
            assertEquals(".uptime", firstValue)
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

        listener.onChannelCommand(connection, message, ".uptime", arrayOf())

        argumentCaptor<ChannelMessage>().apply {
            verify(bot.connection).send(capture())

            assertEquals("Uptime: ", firstValue.text.substring(0, 8))
        }
    }
}