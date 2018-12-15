package com.colacelli.ircbot.base

import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.util.*

interface PropertiesPlugin {
    fun loadProperties(filename: String): Properties {
        var properties = Properties()
        try {
            val file = FileInputStream(filename)

            properties.load(file)
            file.close()
        } catch (e: FileNotFoundException) {
            // Saving file for first time
            saveProperties(filename, properties)
        }

        return properties
    }

    fun saveProperties(filename: String, properties: Properties) {
        val file = FileOutputStream(filename)

        properties.store(file, null)
        file.close()
    }
}