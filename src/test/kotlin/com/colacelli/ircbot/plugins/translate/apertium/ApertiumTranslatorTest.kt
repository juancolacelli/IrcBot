package com.colacelli.ircbot.plugins.translate.apertium

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

internal class ApertiumTranslatorTest {
    private val translator = ApertiumTranslator()

    @Test
    fun translate() = runBlocking {
        withTimeout(10000L) {
            val translation = translator.translate("en", "es", "hello").await()
            assertNotNull(translation)
            assertEquals("hola", translation?.translation)
        }
    }
}