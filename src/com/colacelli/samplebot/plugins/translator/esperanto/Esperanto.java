package com.colacelli.samplebot.plugins.translator.esperanto;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class Esperanto {
    private static final String SEPARATOR = " : ";

    // Downloaded from: https://gitlab.com/sstangl/tuja-vortaro/raw/master/espdic/espdic.txt
    private static final String FILES_PATH = "com/colacelli/samplebot/plugins/translator/esperanto/resources/";
    private static final String DICTIONARY_FILE = FILES_PATH + "espdic.txt";
    private static final String REPLACEMENTS_FILE = FILES_PATH + "replacements.txt";
    private static final String SUFIXES_FILE = FILES_PATH + "sufixes.txt";

    private static HashMap<String, String> dictionary;
    private static HashMap<String, String> replacements;
    private static HashMap<String, String> sufixes;

    public Esperanto() {
        dictionary = new HashMap<>();
        replacements = new HashMap<>();
        sufixes = new HashMap<>();

        // Loading dictionary
        try {
            InputStream inputStream = ClassLoader.getSystemResourceAsStream(DICTIONARY_FILE);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                int separatorIndex = line.indexOf(SEPARATOR);
                if (separatorIndex > -1) {
                    String word = line.substring(0, separatorIndex);
                    String translation = line.substring(separatorIndex + SEPARATOR.length());

                    String purgedWord = purgeWord(word);

                    if (!word.startsWith("-")) {
                        dictionary.put(purgedWord, translation);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Loading replacements
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(getClass().getClassLoader().getResourceAsStream(REPLACEMENTS_FILE));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String line;
            while ((line = bufferedReader.readLine()) != null) {
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
            InputStreamReader inputStreamReader = new InputStreamReader(getClass().getClassLoader().getResourceAsStream(SUFIXES_FILE));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String line;
            while ((line = bufferedReader.readLine()) != null) {
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

    private static String purgeWord(String word) {
        word = word.toLowerCase();
        word = word.replaceAll("[^a-z]", "");

        return word;
    }

    private static String normalizeWord(String word) {
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            String part = entry.getKey();
            String replacement = entry.getValue();

            word = word.replaceAll(part, replacement);
        }

        for (Map.Entry<String, String> entry : sufixes.entrySet()) {
            String sufix = entry.getKey();
            String replacement = entry.getValue();

            if (word.endsWith(sufix)) {
                word = word.replaceAll(sufix + "$", replacement);
            }
        }

        return word;
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
            for (Map.Entry<String, String> entry : dictionary.entrySet()) {
                String esperanto = entry.getKey();
                String english = purgeWord(entry.getValue());

                if (english.equals(word)) {
                    translations.put(english, esperanto);
                }
            }
        }

        return translations;
    }
}
