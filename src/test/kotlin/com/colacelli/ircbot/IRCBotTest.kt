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

internal class IRCBotTest{
    lateinit var user: User
    lateinit var server: Server
    lateinit var bot: IRCBot

    private val plugin = mock(Plugin::class.java)
    private val listener = mock(OnChannelCommandListener::class.java)


    @BeforeEach fun initialize() {
        user = User("Test")
        server = Server("localhost", 6667, false, "")
        bot = IRCBot(server, user)
    }

    @Test
    fun addPlugin() {
        assert(bot.plugins.isEmpty())
        bot.addPlugin(plugin)
        assertEquals(0, bot.plugins.indexOf(plugin))
        verify(plugin).onLoad(bot)
    }

    @Test
    fun removePlugin() {
        bot.addPlugin(plugin)
        bot.removePlugin(plugin)
        assert(bot.plugins.isEmpty())
        verify(plugin).onUnload(bot)
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
}