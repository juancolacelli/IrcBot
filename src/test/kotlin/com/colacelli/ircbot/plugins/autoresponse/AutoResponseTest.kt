package com.colacelli.ircbot.plugins.autoresponse

import com.colacelli.irclib.actors.Channel
import com.colacelli.irclib.actors.User
import com.colacelli.irclib.messages.ChannelMessage
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

internal class AutoResponseTest {
    private lateinit var autoResponse: AutoResponse

    @BeforeEach
    fun initialize() {
        val properties = Properties()
        properties.setProperty("hi", "hello")
        properties.setProperty("bye", "goodbye")
        autoResponse = AutoResponse(properties)
    }

    @Test
    fun add() {
        assertEquals(2, autoResponse.list().size)
        autoResponse.add("hey", "what?")
        assertEquals(3, autoResponse.list().size)
    }

    @Test
    fun del() {
        autoResponse.del("hi")
        assertEquals(1, autoResponse.list().size)
    }

    @Test
    fun get() {
        val message = ChannelMessage(Channel("#test"), "hi", User("test"))
        assertEquals("hello", autoResponse.get(message))
    }

    @Test
    fun get_relacements() {
        autoResponse.add("hi", "hi \$nick welcome to \$channel")
        val message = ChannelMessage(Channel("#test"), "hi", User("test"))
        assertNotNull(autoResponse.get(message))
        val text = autoResponse.get(message)!!.replace("\$nick", "test").replace("\$channel", "#test")
        assertEquals("hi test welcome to #test", text)
    }

    @Test
    fun get_bugFixUppercase() {
        autoResponse.add("hi", "hello")
        val message = ChannelMessage(Channel("#test"), "HI", User("test"))
        assertEquals("hello", autoResponse.get(message))
    }

    @Test
    fun get_regEx() {
        autoResponse.add("hello(.+)?", "hi \$nick")
        var message = ChannelMessage(Channel("#test"), "hello everyone", User("test"))
        assertEquals("hi test", autoResponse.get(message))
    }

    @Test
    fun get_regExReplacements() {
        autoResponse.add("ping (.+)", "pong $1")
        var message = ChannelMessage(Channel("#test"), "ping 1234", User("test"))
        assertEquals("pong 1234", autoResponse.get(message))
    }

    @Test
    fun get_regExReplacementsUppercase() {
        autoResponse.add("ping (.+)", "pong $1")
        val message = ChannelMessage(Channel("#test"), "PING 1234", User("test"))
        assertEquals("pong 1234", autoResponse.get(message))
    }

    @Test
    fun get_regExRepetitionError() {
        autoResponse.add("\\.", "{{{(>_<)}}}")
        val message = ChannelMessage(Channel("#test"), ".", User("test"))
        assertEquals("{{{(>_<)}}}", autoResponse.get(message))
    }

    @Test
    fun list() {
        assertEquals(2, autoResponse.list().size)
    }
}