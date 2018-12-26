package com.colacelli.ircbot.base

import com.colacelli.ircbot.base.listeners.OnChannelCommandListener
import com.colacelli.irclib.connection.Connection
import com.colacelli.irclib.messages.ChannelMessage
import com.nhaarman.mockitokotlin2.mock
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class HelpTest {
    val listener = object : OnChannelCommandListener {
        override val command = ".test"
        override val aliases = arrayOf(".t", ".te")
        override val level = Access.Level.ROOT
        override val help = Help(this, "Help test", "t1", "t2")

        override fun onChannelCommand(connection: Connection, message: ChannelMessage, command: String, args: Array<String>) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }

    @Test
    fun getCommand() {
        assertEquals(".test", listener.help.command)
    }

    @Test
    fun getAliases() {
        assertEquals(".t .te", listener.help.aliases!!.joinToString(" "))
    }

    @Test
    fun getLevel() {
        assertEquals(Access.Level.ROOT, listener.help.level)
    }
}