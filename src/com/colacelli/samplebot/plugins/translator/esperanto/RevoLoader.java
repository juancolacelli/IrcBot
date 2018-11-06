package com.colacelli.samplebot.plugins.translator.esperanto;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

class RevoLoader {
    private ArrayList<Thread> threads;

    RevoLoader() {
        threads = new ArrayList<>();
    }

    void load(String path) {
        Runnable task = new RevoFolderLoader(path);
        Thread worker = new Thread(task);
        worker.setName("RevoFolderLoader");
        worker.start();

        threads.add(worker);
    }

    class RevoFolderLoader implements Runnable {
        private String path;

        public RevoFolderLoader(String path) {
            this.path = path + "/xml";
        }

        @Override
        public void run() {
            File folder = new File(path);

            for (File file : folder.listFiles()) {
                if (file.isFile()) {
                    Runnable task = new RevoFileLoader(file);
                    Thread worker = new Thread(task);
                    worker.setName(file.getName());
                    worker.start();

                    threads.add(worker);
                }
            }

            EsperantoTranslator.getInstance().setLoaded(true);
        }
    }

    class RevoFileLoader implements Runnable {
        private static final String BASE_WORD_TAG = "rad";
        private static final String BASE_WORD_NODES = "tld";

        private static final String DEFINITION_TAG = "drv";
        private static final String SUB_DEFINITION_TAG = "snc";
        private static final String WORD_TAG = "kap";
        private static final String TRANSLATION_GROUP_TAG = "trdgrp";
        private static final String TRANSLATION_TAG = "trd";
        File file;

        public RevoFileLoader(File file) {
            this.file = file;
        }

        private String purgeWord(String word) {
            word = word.toLowerCase();
            word = word.replaceAll("\\(.+", "");
            word = word.replaceAll("(\\*|\n|\r|\t)", "");
            word = word.replaceAll(",.+", "");
            word = word.replaceAll("^( )+", "");
            word = word.replaceAll("( )+$", "");
            word = word.replaceAll("( )+", " ");
            return word;
        }

        private String purgeTranslation(String translation) {
            translation = translation.toLowerCase();
            translation = translation.replaceAll("(\n|\r|\t)", "");
            translation = translation.replaceAll("^( )+", "");
            translation = translation.replaceAll("( )+$", "");
            translation = translation.replaceAll("( )+", " ");
            return translation;
        }

        private void addTranslation(String locale, String word, String translation) {
            EsperantoTranslator.getInstance().addTranslation("eo-" + locale, word, purgeTranslation(translation));

            // Ignore translations with spaces
            translation = purgeWord(translation);
            if (translation.indexOf(" ") == -1) {
                EsperantoTranslator.getInstance().addTranslation(locale + "-eo", translation, word);
            }
        }

        private void parseTranslationGroup(String word, Node group) {
            NodeList nodes = group.getChildNodes();
            String locale = group.getAttributes().item(0).getTextContent();

            for (int i = 0; i < nodes.getLength(); i++) {
                Node node = nodes.item(i);

                if (node.getNodeName().equals(TRANSLATION_TAG)) {
                    addTranslation(locale, word, node.getTextContent());
                }
            }
        }

        private void parseNodes(String word, NodeList nodes) {
            for (int i = 0; i < nodes.getLength(); i++) {
                Node node = nodes.item(i);

                switch (node.getNodeName()) {
                    case DEFINITION_TAG:
                    case SUB_DEFINITION_TAG:
                        parseNodes(word, node.getChildNodes());
                        break;

                    case WORD_TAG:
                        word = purgeWord(node.getTextContent());
                        break;

                    case TRANSLATION_GROUP_TAG:
                        parseTranslationGroup(word, node);
                        break;

                    case TRANSLATION_TAG:
                        String locale = node.getAttributes().item(0).getTextContent();
                        String translation = node.getTextContent();

                        addTranslation(locale, word, translation);
                        break;
                }
            }
        }

        @Override
        public void run() {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            try {
                DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                Document document = documentBuilder.parse(file);

                // Finding base word <rad>bezon</rad>
                String baseWord = document.getElementsByTagName(BASE_WORD_TAG).item(0).getTextContent();

                // Replacing <tld/> with <tld>bezon</tld>
                NodeList baseWordNodes = document.getElementsByTagName(BASE_WORD_NODES);
                for (int i = 0; i < baseWordNodes.getLength(); i++) {
                    baseWordNodes.item(i).setTextContent(baseWord);
                }

                /*
                    <drv>
                        <kap><tld/>i</kap>
                        <snc>
                            <trdgrp lng="en">
                                <trd>want</trd>
                            </trdgrp>
                            <trd lng="en">need</trd>
                        </snc>
                        <trd lng="es">necesitar</trd>
                    </drv>
                */
                NodeList drvs = document.getElementsByTagName(DEFINITION_TAG);
                parseNodes(null, drvs);
            } catch (ParserConfigurationException | SAXException | IOException e) {
                e.printStackTrace();
            }
        }
    }
}
