package ru.performanceLab;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        while (true) {
            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine();

            if (input.equals("--help") || input.equals("-h")) {
                System.out.println("Для вывода актуального курса доллара введите USD");
                System.out.println("Для вывода актуального курса евро введите EUR");
                System.out.println("Для вывода актуального курса фунта введите GBP");
                System.out.println("Для выхода из программы введите: \"-e\" или \"--exit\"");

            } else if (input.equals("-e") || input.equals("--exit")) {
                scanner.close();
                System.exit(0);
            } else if (input.equals("USD") || input.equals("EUR") || input.equals("GBP")) {
                FileDownload fileDownload = new FileDownload();
                fileDownload.run();
                printCurrency(input);
            } else {
                System.out.println("Вы ввели неверную команду");
            }
        }
    }

    public static void printCurrency(String currency) {
        try {
            File currencyFile = new File("XML_daily.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newDefaultInstance();
            DocumentBuilder dBuild = dbFactory.newDocumentBuilder();
            Document doc = dBuild.parse(currencyFile);
            NodeList nodeList = doc.getElementsByTagName("Valute");
            int currencyCode;
            if (currency.equals("USD")) {
                currencyCode = 10;
            } else if (currency.equals("EUR")) {
                currencyCode = 11;
            } else {
                currencyCode = 2;
            }
            Node node = nodeList.item(currencyCode);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                System.out.println("Курс валют для " +
                        element.getElementsByTagName("CharCode").item(0).getTextContent() + ": " +
                        element.getElementsByTagName("Value").item(0).getTextContent() +
                        " рублей");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class FileDownload extends Thread {
        private int readBytes = 0;
        private boolean doStop = false;

        public void run() {
            try {
                URL url = new URL("http://www.cbr.ru/scripts/XML_daily.asp");

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.connect();
                BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
                File f1 = new File("XML_daily.xml");
                FileOutputStream fw = new FileOutputStream(f1);

                byte[] btBuffer = new byte[1024];
                int intRead = 0;
                while ((intRead = bis.read(btBuffer)) != -1) {
                    fw.write(btBuffer, 0, intRead);
                    readBytes = readBytes + intRead;
                }
                fw.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}

