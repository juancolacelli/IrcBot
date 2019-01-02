package com.colacelli.ircbot.plugins.pluginloader

import com.colacelli.ircbot.IRCBot
import com.colacelli.ircbot.base.Access
import com.colacelli.ircbot.base.Helper
import com.colacelli.ircbot.base.listeners.OnChannelCommandListener
import com.colacelli.ircbot.plugins.help.HelpPlugin
import com.colacelli.irclib.actors.Channel
import com.colacelli.irclib.actors.User
import com.colacelli.irclib.connection.Connection
import com.colacelli.irclib.messages.ChannelMessage
import com.nhaarman.mockitokotlin2.*
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class PluginLoaderPluginTest {
    private val connection = mock<Connection>()
    private val access = mock<Access> {
        on { get(any()) } doReturn Access.Level.ROOT
    }
    private val ircbot = IRCBot(mock(), mock())
    private val bot = spy(ircbot) {
        on { helper } doReturn Helper()
        on { connection } doReturn connection
        on { access } doReturn access
        on { pluginLoader } doReturn mock()
    }
    private val plugin = PluginLoaderPlugin()

    @Test
    fun getName() {
        assertEquals("plugin_loader", plugin.name)
    }

    @Test
    fun onLoad() {
        plugin.onLoad(bot)
        argumentCaptor<OnChannelCommandListener>().apply {
            verify(bot, times(3)).addListener(capture())

            assertEquals(".pluginLoad", firstValue.command)
            assertEquals(Access.Level.ROOT, firstValue.level)
            assertEquals(".load .plug+", firstValue.aliases!!.joinToString(" "))

            assertEquals(".pluginUnload", secondValue.command)
            assertEquals(Access.Level.ROOT, secondValue.level)
            assertEquals(".plug- .unload", secondValue.aliases!!.joinToString(" "))

            assertEquals(".pluginList", thirdValue.command)
            assertEquals(Access.Level.ROOT, thirdValue.level)
            assertEquals(".plug .plugList", thirdValue.aliases!!.joinToString(" "))
        }
    }

    @Test
    fun onUnload() {
        plugin.onUnload(bot)
        argumentCaptor<Array<String>>().apply {
            verify(bot).removeListenersByCommands(capture())

            assertEquals(".pluginLoad", firstValue[0])
            assertEquals(".pluginUnload", firstValue[1])
            assertEquals(".pluginList", firstValue[2])
        }
    }

    @Test
    fun commands() {
        var loadListener : OnChannelCommandListener
        var unloadListener : OnChannelCommandListener
        var listListener : OnChannelCommandListener

        val message = mock<ChannelMessage> {
            on { channel } doReturn Channel("#test")
            on { sender } doReturn User("r")
        }

        val pluginSpy = spy<PluginLoaderPlugin>()

        pluginSpy.onLoad(bot)
        argumentCaptor<OnChannelCommandListener>().apply {
            verify(bot, times(3)).addListener(capture())

            loadListener = firstValue
            unloadListener = secondValue
            listListener = thirdValue
        }

        bot.pluginLoader.add(HelpPlugin())

        unloadListener.onChannelCommand(connection, message, ".pluginUnload", arrayOf("help"))
        verify(bot.pluginLoader).unload("help")
        loadListener.onChannelCommand(connection, message, ".pluginLoad", arrayOf("help"))
        verify(bot.pluginLoader).load("help")
        listListener.onChannelCommand(connection, message, ".pluginList", arrayOf())
        verify(bot.pluginLoader).list()
    }
}