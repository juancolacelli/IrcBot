package com.colacelli.ircbot.plugins.translate.apertium

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.*
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach

internal class ApertiumTranslatorTest {
    private val translator = mock<ApertiumTranslator> {
        on { translate("en", "es", "hello") } doReturn GlobalScope.async { ApertiumTranslation("en", "es", "hello", "hola") }
    }

    @Test
    fun translate() {
        runBlocking {
            val translation = translator.translate("en", "es", "hello").await()
            assertNotNull(translation)
            assertEquals("hola", translation!!.translation)
        }
    }
}