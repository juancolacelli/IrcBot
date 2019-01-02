package com.colacelli.ircbot

import com.colacelli.ircbot.base.listeners.OnChannelCommandListener
import com.colacelli.irclib.actors.User
import com.colacelli.irclib.connection.Server
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class IRCBotTest {
    private lateinit var user: User
    private lateinit var server: Server
    private lateinit var bot: IRCBot

    private val listener = mock<OnChannelCommandListener> {
        on { command } doReturn ".test"
        on { help } doReturn mock()
    }


    @BeforeEach
    fun initialize() {
        server = Server("localhost", 6667, false, "")
        user = User("Test")
        bot = IRCBot(server, user)
    }

    @Test
    fun addListener() {
        assert(bot.listeners.isEmpty())
        bot.addListener(listener)
        assertEquals(0, bot.listeners.indexOf(listener))
    }

    @Test
    fun removeListener() {
        bot.addListener(listener)
        bot.removeListener(listener)
        assert(bot.listeners.isEmpty())
    }

    @Test
    fun removeListenerByCommand() {
        assert(bot.listeners.isEmpty())
        bot.addListener(listener)
        bot.removeListenerByCommand(listener.command)
        assert(bot.listeners.isEmpty())
    }

    @Test
    fun removeListenersByCommands() {
        assert(bot.listeners.isEmpty())
        bot.addListener(listener)
        bot.removeListenersByCommands(arrayOf(listener.command))
        assert(bot.listeners.isEmpty())
    }
}