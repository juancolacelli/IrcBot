package com.colacelli.ircbot.plugins.joinpart

import com.colacelli.ircbot.base.PropertiesPlugin
import java.util.*
import kotlin.collections.ArrayList

class ChannelsManager() : PropertiesPlugin {

    private var properties = loadProperties(PROPERTIES_FILE)

    constructor(injectedProperties: Properties) : this() {
        properties = injectedProperties
    }

    companion object {
        const val PROPERTIES_FILE = "channels.properties"
        const val CHANNELS_PROPERTY = "channels"
        const val CHANNELS_SEPARATOR = ","
    }

    fun add(channelName: String): Boolean {
        val channels = list()
        return if (channels.indexOf(channelName) == -1) {
            channels.add(channelName)
            properties.setProperty(CHANNELS_PROPERTY, channels.joinToString(CHANNELS_SEPARATOR))
            saveProperties(PROPERTIES_FILE, properties)
            true
        } else {
            false
        }
    }

    fun del(channelName: String): Boolean {
        val channels = list()
        return if (channels.indexOf(channelName) > -1) {
            channels.remove(channelName)
            properties.setProperty(CHANNELS_PROPERTY, channels.joinToString(CHANNELS_SEPARATOR))
            saveProperties(PROPERTIES_FILE, properties)
            true
        } else {
            false
        }
    }

    fun list(): ArrayList<String> {
        val channels = ArrayList<String> ()
        properties.getProperty(CHANNELS_PROPERTY, "").split(CHANNELS_SEPARATOR).forEach {
            channels.add(it)
        }

        return channels
    }
}