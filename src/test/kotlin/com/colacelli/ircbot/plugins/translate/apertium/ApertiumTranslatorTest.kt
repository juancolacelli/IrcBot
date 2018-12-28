package com.colacelli.ircbot.plugins.translate.apertium

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class ApertiumTranslatorTest {
    private val translator = ApertiumTranslator()

    @Test
    fun translate() {
        runBlocking {
            val translation = translator.translate("en", "es", "hello").await()
            assertNotNull(translation)
            assertEquals("hola", translation!!.translation)
        }
    }
}