package com.colacelli.ircbot.plugins.apertiumtranslate

interface OnApertiumTranslateResult {
    fun onSuccess(translation: ApertiumTranslatePlugin.ApertiumTranslation)
    fun onError()
}