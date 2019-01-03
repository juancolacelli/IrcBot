package com.colacelli.ircbot.plugins.autoresponse

import com.colacelli.ircbot.base.PropertiesPlugin
import com.colacelli.irclib.messages.ChannelMessage
import java.util.*
import java.util.regex.PatternSyntaxException
import kotlin.collections.HashMap

class AutoResponse() : PropertiesPlugin {
    private var properties = loadProperties(PROPERTIES_FILE)

    constructor(injectedProperties: Properties) : this() {
        properties = injectedProperties
    }

    companion object {
        const val PROPERTIES_FILE = "auto_response.properties"
    }

    fun add(trigger: String, response: String) {
        properties.setProperty(trigger.toLowerCase(), response)
        saveProperties(PROPERTIES_FILE, properties)
    }

    fun del(trigger: String) {
        properties.remove(trigger.toLowerCase())
        saveProperties(PROPERTIES_FILE, properties)
    }

    fun get(message: ChannelMessage): String? {
        val text = message.text
        var trigger = Regex("")
        var response = ""

        properties.forEach { key, value ->
            if (response.isBlank()) {
                trigger = try {
                    Regex(key.toString().toLowerCase())
                } catch (e: PatternSyntaxException) {
                    // Invalid Regex saved
                    Regex.fromLiteral(key.toString().toLowerCase())
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
                } catch (e: IndexOutOfBoundsException) {
                    // $2 not found!
                }
            }
        }

        return response
    }

    fun list(): SortedMap<String, String> {
        val responses = HashMap<String, String>()

        properties.forEach { key, value ->
            if (value.toString().isNotBlank()) responses[key.toString()] = value.toString()
        }
        return responses.toSortedMap()
    }
}