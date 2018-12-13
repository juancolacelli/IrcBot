package com.colacelli.ircbot.plugins.apertiumtranslate

interface OnApertiumTranslateResultListener {
    fun onSuccess(translation: ApertiumTranslatePlugin.ApertiumTranslation)
    fun onError()
}