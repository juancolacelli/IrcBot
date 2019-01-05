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
        var trigger: Regex
        var replacement: Regex?
        var response = ""

        properties.forEach { key, value ->
            trigger = try {
                key.toString().toRegex(RegexOption.IGNORE_CASE)
            } catch (e: PatternSyntaxException) {
                key.toString().toRegex(setOf(RegexOption.IGNORE_CASE, RegexOption.LITERAL))
            }

            if (response.isEmpty() && trigger.matches(text)) {
                replacement = value.toString()
                        .replace("\$nick", message.sender!!.nick)
                        .replace("\$channel", message.channel.name)
                        .toRegex(RegexOption.IGNORE_CASE)

                response = text.replace(trigger, replacement.toString())
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