package com.colacelli.ircbot.plugins.esperantotranslator.esperanto;

import java.util.ArrayList;
import java.util.HashMap;

public class EsperantoTranslator {
    private static EsperantoTranslator instance;
    private static HashMap<String, HashMap<String, ArrayList<String>>> translations;
    private boolean loaded = false;

    public EsperantoTranslator() {
        translations = new HashMap<>();
    }

    public static EsperantoTranslator getInstance() {
        if (instance == null) instance = new EsperantoTranslator();

        return instance;
    }

    public void load(String revoPath) {
        RevoLoader revoLoader = new RevoLoader();
        revoLoader.load(revoPath);
    }

    public boolean isLoaded() {
        return loaded;
    }

    void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    void addTranslation(String locale, String word, String translation) {
        translations.putIfAbsent(locale, new HashMap<>());
        translations.get(locale).putIfAbsent(word, new ArrayList<>());


        if (!translations.get(locale).get(word).contains(translation)) {
            translations.get(locale).get(word).add(translation);
        }
    }

    public ArrayList<String> translate(String locale, String word) {
        if (translations.containsKey(locale) && translations.get(locale).containsKey(word)) {
            return translations.get(locale).get(word);
        } else {
            return null;
        }
    }

}
