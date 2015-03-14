package com.dotin.socket.client;

import org.apache.log4j.*;
import org.apache.log4j.xml.DOMConfigurator;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.Socket;

import org.apache.log4j.MDC;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;

public class Client implements Runnable {
    private int port;
    private String requestData;
    private ClientJson clientJson = new ClientJson();
    private String xmlOutputFile;
    private String xmlInputFile;
    private String logFileName;
    ArrayList<String> arrayList = new ArrayList<String>();
    //static Logger logger = Logger.getLogger(Client.class);
    static final Logger logger1 = Logger.getLogger("file");
    static final Logger logger2 = Logger.getLogger("another");

    public synchronized void changeLogPath() {
        Properties props = new Properties();
        try {
            props.load(new FileInputStream("./src/main/java/com/dotin/socket/client/resources/log4j.properties"));
            String fileName = "./src/main/java/com/dotin/socket/client/resources/" + logFileName;

            if (logFileName.equals("test1.log")) {
                System.out.println("test1");
                props.setProperty("log4j.rootLogger", "DEBUG, stdout, file");
                props.setProperty("log4j.appender.file.File", fileName);
            }

            if (logFileName.equals("test2.log")) {
                System.out.println("test2");
                props.setProperty("log4j.rootLogger", "DEBUG, stdout, another");
                props.setProperty("log4j.appender.another.File", fileName);
            }
            LogManager.resetConfiguration();
            PropertyConfigurator.configure(props);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Client(String requestData, int port, String xmlInputFile, String xmlOutputFile, String logFileName) {
        synchronized (this) {
            this.requestData = requestData;
            this.port = port;
            this.xmlInputFile = xmlInputFile;
            this.xmlOutputFile = xmlOutputFile;
            this.logFileName = logFileName;
            PropertyConfigurator.configure("./src/main/java/com/dotin/socket/client/resources/log4j.properties");
            //DOMConfigurator.configure("./src/main/java/com/dotin/socket/client/resources/log4j1.xml");
            //System.setProperty("logfilename", "./src/main/java/com/dotin/socket/client/resources/myfile");
        }
    }

    public void test() {

    }

    @Override
    public void run() {
        synchronized (requestData) {
           // test();
            //changeLogPath();
            //System.setProperty("logfilename", "./src/main/java/com/dotin/socket/client/resources/"+logFileName);
            String xmlPath = "./src/main/java/com/dotin/socket/client/resources/" + xmlOutputFile;
            File xmlFile = new File(xmlPath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder;
            String response = null;
            Socket client = null;
            try {
                client = new Socket("localhost", port);
            } catch (IOException e) {
                e.printStackTrace();
            }
            assert client != null;
            DataOutputStream request = null;
            try {

                request = new DataOutputStream(client.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }

            assert request != null;
            try {
                request.writeUTF(requestData);
            } catch (IOException e) {
                e.printStackTrace();
            }
            int attemp = 0;
            boolean received = false;
            DataInputStream receivedResponse = null;
            try {
                receivedResponse = new DataInputStream(client.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            assert receivedResponse != null;
            do {
                DataInputStream dis = null;
                try {
                    dis = new DataInputStream(client.getInputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                assert dis != null;
                try {
                    if (dis.available() > 100) {
                        try {
                            response = dis.readUTF();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        received = true;

                        String[] responses = clientJson.parseResponse(response);
                        if ((responses[0]).equals("warn")) {
                             if (logFileName.equals("thread1.log")) {
                                 //logger.warn(responses[1] + " Your request: " + requestData);
                                 logger1.warn(responses[1] + " Your request: " + requestData);
                             }

                            else logger2.warn(responses[1] + " Your request: " + requestData);
                        } else if ((responses[0]).equals("info")) {

                            if (logFileName.equals("thread1.log")) {
                                logger1.info(responses[1] + " Your request: " + requestData);
                            } else logger2.info(responses[1] + " Your request: " + requestData);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                attemp++;
            } while (attemp < 4 && !received);


            if (!received) {
                response = "There is no response!";
                if (logFileName.equals("thread1.log")) {
                    logger1.error(response + " Your request: " + requestData);
                } else logger2.error(response + " Your request: " + requestData);
            }

            try {
                dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(xmlFile);
                doc.getDocumentElement().normalize();

                //add new element
                addElement(doc, response, requestData);

                //write the updated document to file or console
                doc.getDocumentElement().normalize();
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                DOMSource source = new DOMSource(doc);
                StreamResult result = new StreamResult(new File(xmlPath));
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");

                transformer.transform(source, result);
            } catch (TransformerException e) {
                e.printStackTrace();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void addElement(Document doc, String response, String request) {
        synchronized (this) {
            NodeList transactions = doc.getElementsByTagName("responses");
            Element transaction = null;

            //loop for each employee
            for (int i = 0; i < transactions.getLength(); i++) {
                transaction = (Element) transactions.item(i);
                // transaction=doc.createElement();

                Element transactionElement = doc.createElement("response");
                ClientTransaction clientTransaction = clientJson.parseRequset(request);
                Attr requestType = doc.createAttribute("type");
                requestType.setValue(clientTransaction.getType());

                transactionElement.setAttributeNode(requestType);

                Attr requestId = doc.createAttribute("id");
                requestId.setValue(clientTransaction.getId());

                transactionElement.setAttributeNode(requestId);

                Attr requestAmount = doc.createAttribute("amount");
                requestAmount.setValue(clientTransaction.getAmount());

                transactionElement.setAttributeNode(requestAmount);


                String[] responses = clientJson.parseResponse(response);

                Attr result = doc.createAttribute("response");
                result.setValue(responses[1]);

                //System.out.println(responses[0]);
                /*if ((responses[0]).equals("info")) {
                    Attr newInitial = doc.createAttribute("newBalance");
                    newInitial.setValue(responses[2]);
                    transactionElement.setAttributeNode(newInitial);
                }*/
                //transactions.appendChild(transaction);
                transactionElement.setAttributeNode(result);

                //transactionElement.setAttribute(new Attribute("request",request));
                transaction.appendChild(transactionElement);
            }
        }
    }


    /*private static void deleteElement(Document doc) {
        NodeList employees = doc.getElementsByTagName("Employee");
        Element emp = null;
        //loop for each employee
        for(int i=0; i<employees.getLength();i++){
            emp = (Element) employees.item(i);
            Node genderNode = emp.getElementsByTagName("gender").item(0);
            emp.removeChild(genderNode);
        }

    }

    private static void updateElementValue(Document doc) {
        NodeList employees = doc.getElementsByTagName("Employee");
        Element emp = null;
        //loop for each employee
        for(int i=0; i<employees.getLength();i++){
            emp = (Element) employees.item(i);
            Node name = emp.getElementsByTagName("name").item(0).getFirstChild();
            name.setNodeValue(name.getNodeValue().toUpperCase());
        }
    }

    private static void updateAttributeValue(Document doc) {
        NodeList employees = doc.getElementsByTagName("Employee");
        Element emp = null;
        //loop for each employee
        for(int i=0; i<employees.getLength();i++){
            emp = (Element) employees.item(i);
            String gender = emp.getElementsByTagName("gender").item(0).getFirstChild().getNodeValue();
            if(gender.equalsIgnoreCase("male")){
                //prefix id attribute with M
                emp.setAttribute("id", "M"+emp.getAttribute("id"));
            }else{
                //prefix id attribute with F
                emp.setAttribute("id", "F"+emp.getAttribute("id"));
            }
        }
    }*/

}

