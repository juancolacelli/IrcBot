package com.colacelli.ircbot.plugins.autoresponse

import com.colacelli.ircbot.base.PropertiesPlugin
import com.colacelli.irclib.messages.ChannelMessage
import java.util.*
import java.util.regex.PatternSyntaxException
import kotlin.collections.HashMap

class AutoResponse : PropertiesPlugin {
    var properties = Properties()

    private object Singleton {
        val instance = AutoResponse()
    }

    companion object {
        const val PROPERTIES_FILE = "auto_response.properties"

        val instance by lazy {
            Singleton.instance
        }
    }

    fun add(trigger: String, response: String) {
        properties = loadProperties(PROPERTIES_FILE)
        properties.setProperty(trigger.toLowerCase(), response)
        saveProperties(PROPERTIES_FILE, properties)
    }

    fun del(trigger: String) {
        properties = loadProperties(PROPERTIES_FILE)
        properties.remove(trigger.toLowerCase())
        saveProperties(PROPERTIES_FILE, properties)
    }

    fun get(message: ChannelMessage) : String? {
        properties = loadProperties(PROPERTIES_FILE)

        val text = message.text
        var trigger = Regex("")
        var response = ""

        properties.forEach { key, value ->
            if (response.isBlank()) {
                trigger = try {
                    Regex(key.toString())
                } catch (e: PatternSyntaxException) {
                    // Invalid Regex saved
                    Regex.fromLiteral(key.toString())
                }

                if (text.toLowerCase().matches(trigger)) {
                    response = value.toString()
                }
            }
        }

        when (trigger.pattern.isNotBlank() && response.isNotBlank()) {
            true -> {
                response = response.replace("\$nick", message.sender!!.nick)
                response = response.replace("\$channel", message.channel.name)

                try {
                    response = text.replace(trigger, response)
                } catch (e : IndexOutOfBoundsException) {
                    // $2 not found!
                }
            }
        }

        return response
    }

    fun list() : SortedMap<String, String> {
        properties = loadProperties(PROPERTIES_FILE)
        val responses = HashMap<String, String>()

        properties.forEach { key, value ->
            if (value.toString().isNotBlank()) responses[key.toString()] = value.toString()
        }
        return responses.toSortedMap()
    }
}