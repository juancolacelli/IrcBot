package com.colacelli.ircbot.plugins.autoresponse

import com.colacelli.irclib.actors.Channel
import com.colacelli.irclib.actors.User
import com.colacelli.irclib.messages.ChannelMessage
import org.junit.jupiter.api.Assertions.assertEquals
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
        // TODO: Test RegEx and replaces
        val message = ChannelMessage(Channel("#test"), "hi", User("test"))
        assertEquals("hello", autoResponse.get(message))
    }

    @Test
    fun list() {
        assertEquals(2, autoResponse.list().size)
    }
}