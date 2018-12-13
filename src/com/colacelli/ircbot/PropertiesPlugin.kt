package com.colacelli.ircbot

import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*

interface PropertiesPlugin {
    fun loadProperties(filename : String) : Properties {
        val properties = Properties()
        val file = FileInputStream(filename)

        properties.load(file)
        file.close()

        return properties
    }

    fun saveProperties(filename: String, properties: Properties) {
        val file = FileOutputStream(filename)

        properties.store(file, null)
        file.close()
    }
}