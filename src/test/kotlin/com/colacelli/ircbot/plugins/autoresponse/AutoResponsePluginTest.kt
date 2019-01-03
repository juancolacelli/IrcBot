package com.colacelli.ircbot.plugins.autoresponse

import com.colacelli.ircbot.IRCBot
import com.colacelli.ircbot.base.Access
import com.colacelli.ircbot.base.listeners.OnChannelCommandListener
import com.colacelli.irclib.actors.Channel
import com.colacelli.irclib.actors.User
import com.colacelli.irclib.connection.Connection
import com.colacelli.irclib.connection.listeners.OnChannelMessageListener
import com.colacelli.irclib.messages.ChannelMessage
import com.nhaarman.mockitokotlin2.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class AutoResponsePluginTest {
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
    private val plugin = AutoResponsePlugin()

    @Test
    fun getName() {
        assertEquals("auto_response", plugin.name)
    }

    @Test
    fun onLoad() {
        plugin.onLoad(bot)
        argumentCaptor<OnChannelCommandListener>().apply {
            verify(bot, times(3)).addListener(capture())

            assertEquals(".autoResponseAdd", firstValue.command)
            assertEquals(Access.Level.ADMIN, firstValue.level)
            assertEquals(".ar+ .arAdd", firstValue.aliases!!.joinToString(" "))

            assertEquals(".autoResponseDel", secondValue.command)
            assertEquals(Access.Level.ADMIN, secondValue.level)
            assertEquals(".ar- .arDel", secondValue.aliases!!.joinToString(" "))

            assertEquals(".autoResponseList", thirdValue.command)
            assertEquals(Access.Level.OPERATOR, thirdValue.level)
            assertEquals(".ar .arList", thirdValue.aliases!!.joinToString(" "))
        }
        verify(bot).addListener(any<OnChannelMessageListener>())
    }

    @Test
    fun onUnload() {
        plugin.onUnload(bot)
        argumentCaptor<Array<String>>().apply {
            verify(bot).removeListenersByCommands(capture())
            assertEquals(".autoResponseAdd", firstValue[0])
            assertEquals(".autoResponseDel", firstValue[1])
            assertEquals(".autoResponseList", firstValue[2])
        }
        verify(bot).removeListener(any<OnChannelMessageListener>())
    }

    @Test
    fun behavior() {
        var listener: OnChannelMessageListener

        val autoResponseMock = mock<AutoResponse>()
        val pluginSpy = spy<AutoResponsePlugin> {
            on { autoResponse } doReturn autoResponseMock
        }
        pluginSpy.onLoad(bot)
        argumentCaptor<OnChannelMessageListener>().apply {
            verify(bot).addListener(capture())

            listener = firstValue
        }

        val message = ChannelMessage(Channel("#test"), "hi", User("test"))
        listener.onChannelMessage(connection, message)
        verify(autoResponseMock).get(message)
    }
}