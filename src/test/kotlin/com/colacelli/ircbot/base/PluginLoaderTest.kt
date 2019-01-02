package com.colacelli.ircbot.base

import com.colacelli.ircbot.plugins.access.AccessPlugin
import com.colacelli.ircbot.plugins.help.HelpPlugin
import com.nhaarman.mockitokotlin2.mock
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class PluginLoaderTest {
    private val loader = PluginLoader(mock())

    @Test
    fun add() {
        assertEquals(0, loader.list().size)
        loader.add(HelpPlugin())
        assertEquals(1, loader.list().size)
        loader.add(AccessPlugin())
        assertEquals(2, loader.list().size)
    }

    @Test
    fun unload() {
        loader.add(HelpPlugin())
        loader.add(AccessPlugin())
        loader.unload("help")
        loader.unload("access")
        assertEquals(0, loader.list(true).size)
    }

    @Test
    fun load() {
        loader.add(HelpPlugin())
        loader.add(AccessPlugin())
        loader.unload("help")
        loader.unload("access")
        loader.load("help")
        loader.load("access")
        assertEquals(2, loader.list(true).size)
    }

    @Test
    fun list() {
        loader.add(HelpPlugin())
        loader.add(AccessPlugin())
        assertEquals("help access", loader.list().joinToString(" "))
    }
}