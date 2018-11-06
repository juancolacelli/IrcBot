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
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

class RevoLoader {
    private ArrayList<Thread> threads;

    RevoLoader() {
        threads = new ArrayList<>();
    }

    void load() {
        Runnable task = new RevoFolderLoader();
        Thread worker = new Thread(task);
        worker.setName("RevoFolderLoader");
        worker.start();

        threads.add(worker);
    }

    class RevoFolderLoader implements Runnable {
        private static final String REVO_PATH = "com/colacelli/samplebot/plugins/translator/esperanto/revo/xml/";

        @Override
        public void run() {
            try {
                URL resource = getClass().getClassLoader().getResource(REVO_PATH);
                File folder = new File(resource.toURI());

                for(File file : folder.listFiles()) {
                    if(file.isFile()) {
                        Runnable task = new RevoFileLoader(file);
                        Thread worker = new Thread(task);
                        worker.setName(file.getName());
                        worker.start();

                        threads.add(worker);
                    }
                }

                EsperantoTranslator.getInstance().setLoaded(true);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    class RevoFileLoader implements Runnable {
        private static final String BASE_WORD_TAG = "rad";
        private static final String BASE_WORD_NODES = "tld";
        private static final String DEFINITION_TAG = "drv";
        private static final String WORD_TAG = "kap";
        private static final String TRANSLATION_TAG = "trd";
        File file;

        public RevoFileLoader(File file) {
            this.file = file;
        }

        private String purgeWord(String word) {
            word = word.replaceAll("(\\*|\n|\r|\t)", "");
            word = word.replaceAll("^( )+", "");
            word = word.replaceAll("( )+$", "");
            return word.toLowerCase();
        }

        @Override
        public void run() {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            try {
                DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                Document document = documentBuilder.parse(file);

                String baseWord = document.getElementsByTagName(BASE_WORD_TAG).item(0).getTextContent();

                NodeList baseWordNodes = document.getElementsByTagName(BASE_WORD_NODES);
                for(int i = 0; i < baseWordNodes.getLength(); i++) {
                    baseWordNodes.item(i).setTextContent(baseWord);
                }

                // <drv><kap><tld/>i<trd lng="es">necesitar...
                NodeList drvs = document.getElementsByTagName(DEFINITION_TAG);
                String word = null;
                for(int i = 0; i < drvs.getLength(); i++) {
                    Node drv = drvs.item(i);
                    NodeList drvNodes = drv.getChildNodes();

                    for(int j = 0; j < drvNodes.getLength(); j++) {
                        Node drvNode = drvNodes.item(j);

                        if(drvNode.getNodeName().equals(WORD_TAG)) {
                            // Word
                            word = purgeWord(drvNode.getTextContent());
                        } else if(drvNode.getNodeName().equals(TRANSLATION_TAG)) {
                            // Translation
                            String translation = purgeWord(drvNode.getTextContent());
                            String locale = drvNode.getAttributes().item(0).getTextContent();

                            final String eoLocale = "eo-" + locale;
                            final String localeEo = locale + "-eo";

                            EsperantoTranslator.getInstance().addTranslation(eoLocale, word, translation);
                            EsperantoTranslator.getInstance().addTranslation(localeEo, translation, word);
                        }
                    }
                }
            } catch (ParserConfigurationException | SAXException | IOException e) {
                e.printStackTrace();
            }
        }
    }
}
