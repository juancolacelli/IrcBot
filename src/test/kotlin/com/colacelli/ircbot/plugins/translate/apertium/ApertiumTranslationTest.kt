package com.colacelli.ircbot.plugins.translate.apertium

import com.google.gson.Gson
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class ApertiumTranslationTest {
    private val json = "{\"translatedText\": \"hola\"}"
    private val translation = Gson().fromJson(json, ApertiumTranslation::class.java)

    @Test
    fun getTranslation() {
        assertEquals("hola", translation.translation)
    }
}