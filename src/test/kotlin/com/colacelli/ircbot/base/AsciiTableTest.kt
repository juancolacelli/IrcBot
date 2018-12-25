package com.colacelli.ircbot.base

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach

internal class AsciiTableTest {
    @BeforeEach fun initialize() {
    }

    @Test
    fun toText() {
        val texts = ArrayList<Array<String>>()
        texts.add(arrayOf("c1", "c2", "c3"))
        texts.add(arrayOf("c1a", "c2a", "c3a"))
        val titles = arrayOf("t1", "t2", "t3")
        val asciiTable = AsciiTable(titles, texts)
        val toText = asciiTable.toText()

        val iterator = toText.iterator()

        var line = "-------------------"
        assertEquals(iterator.next(), line)
        assertEquals(iterator.next(), "| t1  | t2  | t3  |")
        assertEquals(iterator.next(), line)
        assertEquals(iterator.next(), "| c1  | c2  | c3  |")
        assertEquals(iterator.next(), "| c1a | c2a | c3a |")
        assertEquals(iterator.next(), line)
    }
}