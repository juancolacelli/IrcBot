package com.colacelli.ircbot.base

import com.colacelli.ircbot.base.listeners.OnChannelCommandListener
import com.colacelli.irclib.connection.Connection
import com.colacelli.irclib.messages.ChannelMessage
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach

internal class HelperTest {
    private lateinit var helper : Helper
    private val listener1 = object : OnChannelCommandListener {
        override val command = ".test"
        override val aliases: Nothing? = null
        override val level = Access.Level.USER
        override val help = Help(this, "Tests the helper", "query")

        override fun onChannelCommand(connection: Connection, message: ChannelMessage, command: String, args: Array<String>) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }

    private val listener2 = object : OnChannelCommandListener {
        override val command = ".op"
        override val aliases: Nothing? = null
        override val level = Access.Level.OPERATOR
        override val help = Help(this, "Gives op", "nick")

        override fun onChannelCommand(connection: Connection, message: ChannelMessage, command: String, args: Array<String>) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }
    @BeforeEach fun initialize() {
        helper = Helper()
    }

    @Test
    fun list() {
        helper.addHelp(listener1.help)
        helper.addHelp(listener2.help)
        assertEquals(2, helper.list(Access.Level.ROOT, "").size)
        assertEquals(2, helper.list(Access.Level.ADMIN, "").size)
        assertEquals(2, helper.list(Access.Level.OPERATOR, "").size)
        assertEquals(1, helper.list(Access.Level.USER, "").size)
        assertEquals(1, helper.list(Access.Level.USER, ".test").size)
        assertEquals(0, helper.list(Access.Level.USER, ".foo").size)
    }

    @Test
    fun addHelp() {
        // TODO: Test only the addHelp() without using list()
        helper.addHelp(listener1.help)
        assertEquals(1, helper.list(Access.Level.USER, "").size)
    }

    @Test
    fun removeHelp() {
        helper.addHelp(listener1.help)
        helper.removeHelp(listener1.help)
        assertEquals(0, helper.list(Access.Level.USER, "").size)
    }
}