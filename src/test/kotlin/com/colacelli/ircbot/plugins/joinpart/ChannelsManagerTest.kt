package com.colacelli.ircbot.plugins.joinpart

import com.colacelli.ircbot.plugins.joinpart.ChannelsManager
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import java.util.*

internal class ChannelsManagerTest {
    private lateinit var channelsManager: ChannelsManager

    @BeforeEach
    fun initialize() {
        val properties = Properties()
        properties.setProperty(ChannelsManager.CHANNELS_PROPERTY, "#channel_a${ChannelsManager.CHANNELS_SEPARATOR}#channel_b")
        channelsManager = ChannelsManager(properties)
    }

    @Test
    fun add() {
        assertFalse(channelsManager.add("#channel_a"))
        assert(channelsManager.add("#channel_c"))

        val list = channelsManager.list()
        assertNotEquals(-1, list.indexOf("#channel_c"))
    }

    @Test
    fun del() {
        assertFalse(channelsManager.del("#channel_c"))
        assert(channelsManager.del("#channel_a"))

        val list = channelsManager.list()
        assertEquals(-1, list.indexOf("#channel_a"))
    }

    @Test
    fun list() {
        val list = channelsManager.list()
        assertEquals(2, list.size)
    }
}