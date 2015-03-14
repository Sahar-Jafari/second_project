package com.dotin.socket.client;

import org.apache.log4j.PropertyConfigurator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Dotin school 6 on 3/9/2015.
 */
public class ClientCaller implements Runnable {
    private ArrayList<String> clientTransactions = new ArrayList<String>();
    ClientJson clientJson = new ClientJson();
    private int port;
    private String xmlOutputFile;
    private String xmlInputFile;
    private String logFileName;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }


    private String threadResult;

    public String getThreadResult() {
        return threadResult;
    }

    public void setThreadResult(String threadResult) {
        this.threadResult = threadResult;
    }

    public static void main(String[] args) throws IOException, InterruptedException, ParserConfigurationException {
        Thread clientThread1 = new Thread(new ClientCaller("response.xml", "terminal.xml", "thread1.log"));
        clientThread1.start();

        Thread clientThread2 = new Thread(new ClientCaller("response1.xml", "terminal1.xml", "thread2.log"));
        clientThread2.start();

    }

    public void readXml() throws ParserConfigurationException, IOException, SAXException {
        ClientJson clientJson = new ClientJson();
        File terminalFile = new File("./src/main/java/com/dotin/socket/client/resources/" + xmlInputFile);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document transactionDoc = dBuilder.parse(terminalFile);
        transactionDoc.getDocumentElement().normalize();

        Element serverElement = (Element) transactionDoc.getElementsByTagName("server").item(0);
        port = Integer.parseInt(serverElement.getAttribute("port"));

        NodeList nodeList1 = transactionDoc.getDocumentElement().getElementsByTagName("transactions");
        Node transactionNode = nodeList1.item(0);
        Element transactionElement = (Element) transactionNode;
        Node childNode = transactionElement.getFirstChild();
        while (childNode.getNextSibling() != null) {
            childNode = childNode.getNextSibling();
            if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                Element childElement = (Element) childNode;
                ClientTransaction clientTransaction = new ClientTransaction();
                clientTransaction.setType(childElement.getAttribute("type"));
                clientTransaction.setAmount(childElement.getAttribute("amount"));
                clientTransaction.setId(childElement.getAttribute("deposit"));
                clientTransactions.add(clientJson.writeTransactionJsonObject(clientTransaction));
            }
        }

    }

    public ClientCaller(String xmlOutputFile, String xmlInputFile, String logFileName) {
        synchronized (this) {
            this.xmlOutputFile = xmlOutputFile;
            this.xmlInputFile = xmlInputFile;
            this.logFileName = logFileName;
        }
    }

    @Override
    public void run() {
         //synchronized (this) {
             try {
                 readXml();
             } catch (ParserConfigurationException e) {
                 e.printStackTrace();
             } catch (IOException e) {
                 e.printStackTrace();
             } catch (SAXException e) {
                 e.printStackTrace();
             }

             File logFile = new File("./src/main/java/com/dotin/socket/client/resources/" + logFileName);
             if (logFile.exists()) {
                 if (logFile.delete()) System.out.println(logFile + " has been deleted!");
             }

       File logFile1= new File("./src/main/java/com/dotin/socket/client/resources/" + "log.log");
        if (logFile1.exists()) {
            if(logFile1.delete()) System.out.println(logFile+ " has been deleted!");
        }

             File xmlFile = new File("./src/main/java/com/dotin/socket/client/resources/" + xmlOutputFile);

             if (xmlFile.exists()) {
                 if (xmlFile.delete()) {
                     createXmlFile(xmlFile);
                 }

             } else {
                 createXmlFile(xmlFile);
             }

             Iterator<String> iterator = clientTransactions.iterator();
             while (iterator.hasNext()) {
                 Object obj=iterator.next();
                 Thread clientThread = new Thread(new Client(obj.toString(), port, xmlInputFile, xmlOutputFile, logFileName));
                 //System.out.println(obj.toString()+"   " +logFileName);
                 clientThread.start();
                 try {
                     clientThread.join();
                 } catch (InterruptedException e) {
                     e.printStackTrace();
                 }
             }
        // }
    }

    private void createXmlFile(File xmlFile) {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = null;
        try {
            docBuilder = docFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        // root elements
        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElement("responses");
        doc.appendChild(rootElement);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = null;
        try {
            transformer = transformerFactory.newTransformer();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        }
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File("./src/main/java/com/dotin/socket/client/resources/" + xmlOutputFile));
        assert transformer != null;
        try {
            transformer.transform(source, result);
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }
    // }
}
