package com.colacelli.ircbot.base

import com.colacelli.ircbot.IRCBot
import com.colacelli.irclib.actors.User
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.mockito.Mockito.*
import java.util.*

internal class AccessTest {
    private val bot = mock(IRCBot::class.java)
    private lateinit var properties : Properties
    private lateinit var access: Access

    @BeforeEach fun initialize() {
        properties = Properties()
        properties.setProperty("r", "ROOT")
        properties.setProperty("a", "ADMIN")
        properties.setProperty("o", "OPERATOR")

        access = Access(bot, properties)
    }

    @Test
    fun check() {
    }

    @Test
    fun get() {
        assertEquals(Access.Level.ROOT, access.get(User("r")))
        assertEquals(Access.Level.ADMIN, access.get(User("a")))
        assertEquals(Access.Level.OPERATOR, access.get(User("o")))
        assertEquals(Access.Level.USER, access.get(User("u")))
    }

    @Test
    fun add() {
        access.add("t", Access.Level.ADMIN)
        assertEquals(Access.Level.ADMIN, access.get(User("t")))
    }

    @Test
    fun del() {
        access.del("r")
        assertEquals(Access.Level.USER, access.get(User("r")))
    }

    @Test
    fun list() {
        assertEquals(properties.size, access.list().size)
    }
}