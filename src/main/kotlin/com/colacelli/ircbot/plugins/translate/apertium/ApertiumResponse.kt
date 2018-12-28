package com.colacelli.ircbot.plugins.translate.apertium

import com.google.gson.annotations.SerializedName

class ApertiumResponse(@SerializedName("responseStatus") val status: Int, @SerializedName("responseData") val data: ApertiumTranslation)