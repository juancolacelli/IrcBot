package com.colacelli.ircbot.plugins.translate.apertium

import com.google.gson.Gson
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class ApertiumResponseTest {
    private val json = "{\"responseData\": {\"translatedText\": \"hola\"}, \"responseDetails\": null, \"responseStatus\": 200}"
    private val response = Gson().fromJson(json, ApertiumResponse::class.java)

    @Test
    fun getStatus() {
        assertEquals(200, response.status)
    }

    @Test
    fun getData() {
        assertEquals("hola", response.data.translation)
    }
}