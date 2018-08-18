package com.colacelli.ircbot;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EsperantoTranslator {
    private static final String SEPARATOR = " : ";

    // Downloaded from: https://gitlab.com/sstangl/tuja-vortaro/raw/master/espdic/espdic.txt
    private static final String DICTIONARY_FILE = "src/com/colacelli/ircbot/files/espdic.txt";
    private static final String REPLACEMENTS_FILE = "src/com/colacelli/ircbot/files/replacements.txt";
    private static final String SUFIXES_FILE = "src/com/colacelli/ircbot/files/sufixes.txt";

    private static HashMap<String, String> dictionary;
    private static HashMap<String, String> replacements;
    private static HashMap<String, String> sufixes;

    public EsperantoTranslator() {
        dictionary = new HashMap<>();
        replacements = new HashMap<>();
        sufixes = new HashMap<>();

        // Loading dictionary
        try {
            for(String line : Files.readAllLines(Paths.get(DICTIONARY_FILE))) {
                int separatorIndex = line.indexOf(SEPARATOR);
                if (separatorIndex > -1) {
                    String word = line.substring(0, separatorIndex);
                    String translation = line.substring(separatorIndex + SEPARATOR.length());

                    String purgedWord = purgeWord(word);

                    if(!word.startsWith("-")) {
                        dictionary.put(purgedWord, translation);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Loading replacements
        try {
            for(String line : Files.readAllLines(Paths.get(REPLACEMENTS_FILE))) {
                int separatorIndex = line.indexOf(SEPARATOR);
                if (separatorIndex > -1) {
                    String part = line.substring(0, separatorIndex);
                    String replacement = line.substring(separatorIndex + SEPARATOR.length());
                    replacements.put(part, replacement);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Loading sufixes
        try {
            for(String line : Files.readAllLines(Paths.get(SUFIXES_FILE))) {
                int separatorIndex = line.indexOf(SEPARATOR);
                if (separatorIndex > -1) {
                    String sufix = line.substring(0, separatorIndex);
                    String replacement = line.substring(separatorIndex + SEPARATOR.length());
                    sufixes.put(sufix, replacement);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public HashMap<String, String> translate(String word) {
        HashMap<String, String> translations = new HashMap<>();

        word = purgeWord(word);
        String normalizeWord = normalizeWord(word);

        String translation = dictionary.get(word);
        if (translation != null) {
            // Salutas
            translations.put(word, translation);
        } else if (!normalizeWord.equals(word)) {
            // Saluti
            translation = dictionary.get(normalizeWord);
            if (translation != null) translations.put(normalizeWord, translation);
        }

        if (translations.isEmpty()) {
            // Hello
            for(Map.Entry<String, String> entry : dictionary.entrySet()) {
                String esperanto = entry.getKey();
                String english = purgeWord(entry.getValue());

                if(english.equals(word)) {
                    translations.put(english, esperanto);
                }
            }
        }

        return translations;
    }

    public static String purgeWord(String word) {
        word = word.toLowerCase();
        word = word.replaceAll("[^a-z]", "");

        return word;
    }

    public static String normalizeWord(String word) {
        for(Map.Entry<String, String> entry : replacements.entrySet()) {
            String part = entry.getKey();
            String replacement = entry.getValue();

            word = word.replaceAll(part, replacement);
        }

        for(Map.Entry<String, String> entry : sufixes.entrySet()) {
            String sufix = entry.getKey();
            String replacement = entry.getValue();

            if(word.endsWith(sufix)) {
                word = word.replaceAll(sufix + "$", replacement);
            }
        }

        return word;
    }
}
