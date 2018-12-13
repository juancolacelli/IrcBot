package com.colacelli.ircbot.plugins.autoresponse

import com.colacelli.ircbot.PropertiesPlugin
import com.colacelli.irclib.messages.ChannelMessage
import java.util.*
import kotlin.collections.HashMap

class AutoResponse : PropertiesPlugin {
    var properties = Properties()

    private object Singleton {
        val instance = AutoResponse()
    }

    companion object {
        const val PROPERTIES_FILE = "access.properties"

        val instance by lazy {
            Singleton.instance
        }
    }

    fun set(trigger: String, response: String) {
        properties = loadProperties(PROPERTIES_FILE)
        properties.setProperty(trigger.toLowerCase(), response)
        saveProperties(PROPERTIES_FILE, properties)
    }

    fun get(message: ChannelMessage) : String {
        properties = loadProperties(PROPERTIES_FILE)
        val response = properties.getProperty(message.text.toLowerCase())

        if (response.isNotBlank()) {
            response.replace("\$nick", message.sender.nick)
            response.replace("\$channel", message.channel.name)
        }

        return response
    }

    fun list() : HashMap<String, String> {
        properties = loadProperties(PROPERTIES_FILE)
        val responses = HashMap<String, String>()

        properties.forEach { key, value ->
            if (value.toString().isNotBlank()) responses[key.toString()] = value.toString()
        }

        return responses
    }
}