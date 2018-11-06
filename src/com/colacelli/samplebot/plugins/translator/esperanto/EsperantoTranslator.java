package com.colacelli.samplebot.plugins.translator.esperanto;

import java.util.HashMap;

public class EsperantoTranslator {
    private static EsperantoTranslator instance;
    private static HashMap<String, HashMap<String, String>> translations;
    private boolean loaded = false;

    public static EsperantoTranslator getInstance() {
        if (instance == null) instance = new EsperantoTranslator();

        return instance;
    }

    public boolean isLoaded() {
        return loaded;
    }

    void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    public EsperantoTranslator() {
        translations = new HashMap<>();

        RevoLoader revoLoader = new RevoLoader();
        revoLoader.load();
    }

    void addTranslation(String locale, String word, String translation) {
        translations.putIfAbsent(locale, new HashMap<>());
        translations.get(locale).put(word, translation);
    }

    public String translate(String locale, String word) {
        if (translations.containsKey(locale) && translations.get(locale).containsKey(word)) {
            return translations.get(locale).get(word);
        } else {
            return null;
        }
    }

}
