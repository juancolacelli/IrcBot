package com.colacelli.ircbot.base

import com.colacelli.ircbot.IRCBot
import com.colacelli.ircbot.base.listeners.OnAccessCheckListener
import com.colacelli.irclib.actors.User
import com.colacelli.irclib.connection.Connection
import com.colacelli.irclib.connection.Rawable
import com.colacelli.irclib.connection.listeners.OnRawCodeListener
import com.nhaarman.mockitokotlin2.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

internal class AccessTest {
    private val connection = mock<Connection> {}
    private val bot = mock<IRCBot> {
        on { connection } doReturn connection
    }
    private lateinit var properties: Properties
    private lateinit var access: Access

    @BeforeEach
    fun initialize() {
        properties = Properties()
        properties.setProperty("r", "ROOT")
        properties.setProperty("a", "ADMIN")
        properties.setProperty("o", "OPERATOR")

        access = Access(bot, properties)
    }

    @Test
    fun check_ok() {
        val listener = mock<OnAccessCheckListener>()

        access.check(User("r"), Access.Level.ROOT, listener)
        argumentCaptor<OnRawCodeListener>().apply {
            verify(bot, times(2)).addListener(capture())
            firstValue.onRawCode(connection, "", Rawable.RawCode.WHOIS_IDENTIFIED_NICK.code, arrayOf("", "", "", "r").toList())
        }
        verify(listener).onSuccess(any(), any())
    }

    @Test
    fun check_okSuper() {
        val listener = mock<OnAccessCheckListener>()

        access.check(User("r"), Access.Level.ADMIN, listener)
        argumentCaptor<OnRawCodeListener>().apply {
            verify(bot, times(2)).addListener(capture())
            firstValue.onRawCode(connection, "", Rawable.RawCode.WHOIS_IDENTIFIED_NICK.code, arrayOf("", "", "", "r").toList())
        }
        verify(listener).onSuccess(any(), any())
    }

    @Test
    fun check_nokIdentify() {
        val listener = mock<OnAccessCheckListener>()

        access.check(User("r"), Access.Level.ROOT, listener)
        argumentCaptor<OnRawCodeListener>().apply {
            verify(bot, times(2)).addListener(capture())
            secondValue.onRawCode(connection, "", Rawable.RawCode.WHOIS_END.code, arrayOf("", "", "", "r").toList())
        }
        verify(listener).onError(any(), any())
    }

    @Test
    fun check_okWithoutNickserv() {
        val listener = mock<OnAccessCheckListener>()

        access.checkWithNickServ = false
        access.check(User("r"), Access.Level.ROOT, listener)
        verify(listener).onSuccess(any(), any())
    }

    @Test
    fun check_nok() {
        val listener = mock<OnAccessCheckListener>()

        access.check(User("u"), Access.Level.ROOT, listener)
        verify(listener).onError(any(), any())
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
        access.del("a")
        assertEquals(Access.Level.USER, access.get(User("a")))
    }

    @Test
    fun list() {
        assertEquals(properties.size, access.list().size)
    }
}