package com.colacelli.ircbot

import com.colacelli.ircbot.base.Help
import com.colacelli.ircbot.base.Plugin
import com.colacelli.ircbot.base.listeners.OnChannelCommandListener
import com.colacelli.irclib.actors.User
import com.colacelli.irclib.connection.Server
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*

internal class IRCBotTest {
    private lateinit var user: User
    private lateinit var server: Server
    private lateinit var bot: IRCBot

    private val plugin = mock(Plugin::class.java)
    private val listener = mock(OnChannelCommandListener::class.java)


    @BeforeEach
    fun initialize() {
        server = Server("localhost", 6667, false, "")
        user = User("Test")
        bot = IRCBot(server, user)

        `when`(listener.help).thenReturn(mock(Help::class.java))
    }

    @Test
    fun addPlugin() {
        assert(bot.plugins.isEmpty())
        bot.addPlugin(plugin)
        verify(plugin).onLoad(bot)
        assertEquals(0, bot.plugins.indexOf(plugin))
    }

    @Test
    fun removePlugin() {
        bot.addPlugin(plugin)
        bot.removePlugin(plugin)
        verify(plugin).onUnload(bot)
        assert(bot.plugins.isEmpty())
    }

    @Test
    fun addListener() {
        assert(bot.listeners.isEmpty())
        // FIXME: Fixing listener.help null
        `when`(listener.help).thenReturn(mock(Help::class.java))
        bot.addListener(listener)
        assertEquals(0, bot.listeners.indexOf(listener))
    }

    @Test
    fun removeListener() {
        // FIXME: Fixing listener.help null
        `when`(listener.help).thenReturn(mock(Help::class.java))
        bot.addListener(listener)
        bot.removeListener(listener)
        assert(bot.listeners.isEmpty())
    }

    @Test
    fun removeListener1() {
        assert(bot.listeners.isEmpty())
        // FIXME: Fixing listener.help null
        `when`(listener.help).thenReturn(mock(Help::class.java))
        `when`(listener.command).thenReturn(".test")
        bot.addListener(listener)
        bot.removeListener(listener.command)
        assert(bot.listeners.isEmpty())
    }

    @Test
    fun removeListeners() {
        assert(bot.listeners.isEmpty())
        // FIXME: Fixing listener.help null
        `when`(listener.help).thenReturn(mock(Help::class.java))
        `when`(listener.command).thenReturn(".test")
        bot.addListener(listener)
        bot.removeListeners(arrayOf(listener.command))
        assert(bot.listeners.isEmpty())
    }
}