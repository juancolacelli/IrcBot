package com.colacelli.ircbot.plugins.translate.apertium

import com.google.gson.annotations.SerializedName

class ApertiumTranslation(var localeA: String, var localeB: String, var text: String, @SerializedName("translatedText") val translation: String)