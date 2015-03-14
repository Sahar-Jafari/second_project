package com.dotin.socket.server.model.bl;

import com.dotin.socket.server.model.to.DepositTo;
import com.dotin.socket.server.model.to.ServerTransactionTo;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.*;
import java.math.BigDecimal;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Dotin school 6 on 3/7/2015.
 */
public class ThreadPooledServer implements Runnable {
    final static Logger logger = Logger.getLogger(ThreadPooledServer.class);
    protected int serverPort = 8080;
    protected ServerSocket serverSocket = null;
    protected boolean isStopped = false;
    protected Thread runningThread = null;
    ServerJson serverJson = new ServerJson();
    private volatile String id = "lock";
    //private final Lock lock = new ReentrantLock(true);

    protected ExecutorService threadPool = Executors.newFixedThreadPool(10);
    //protected
    private HashMap<String, DepositTo> depositsHashMap = null;

    public ThreadPooledServer(int port, HashMap<String, DepositTo> depositsHashMap) {
        this.depositsHashMap = depositsHashMap;
        this.serverPort = port;
        openServerSocket();
        PropertyConfigurator.configure("./src/main/java/com/dotin/socket/server/resources/log4j.properties");
    }

    public void run() {

        //Transaction transaction = new Transaction(this.depositsHashMap);
        synchronized (this) {
            this.runningThread = Thread.currentThread();

        }
        //openServerSocket();
        while (!isStopped()) {
            Socket clientSocket = null;
            try {


                    clientSocket = this.serverSocket.accept();
                    InputStream inFromServer = clientSocket.getInputStream();
                    DataInputStream in = new DataInputStream(inFromServer);
                    String response = null;
                    String request = in.readUTF();
                    DataValidator dataValidator = new DataValidatorImpl();
                    ServerTransactionTo serverTransactionTo = serverJson.parseReceivedData(request);

                synchronized (id) {
                    id = serverTransactionTo.getId();
                    DepositTo value = depositsHashMap.get(id);
                    String customerName = value.getCustomerName();
                    BigDecimal initialBalance = value.getInitialBalance();
                    BigDecimal upperBound = value.getUpperBound();
                    String type = serverTransactionTo.getType();
                    BigDecimal amount = serverTransactionTo.getAmount();
                    try {
                        if (type.equals("withdraw")) {
                            dataValidator.validWithDraw(initialBalance, amount);
                            value.setInitialBalance(initialBalance.subtract(amount));
                            depositsHashMap.put(id, value);
                        }
                        if (type.equals("deposit")) {
                            dataValidator.validDeposit(initialBalance, amount, upperBound);
                            value.setInitialBalance(initialBalance.add(amount));
                            depositsHashMap.put(id, value);
                        }
                        final String orgName = Thread.currentThread().getName();
                        response = serverJson.writeTransactionInfoObject("info", "Your request has been done successfully!");
                        //response = serverJson.writeTransactionInfoObject("info", "Your request has been done successfully!" ,((depositsHashMap.get(id)).getInitialBalance()).toString());

                    } catch (Exception ex) {
                        response = serverJson.writeTransactionWarnObject("warn", ex.getMessage());
                        //ex.printStackTrace();
                    }

                    DataOutputStream outToClient = new DataOutputStream(clientSocket.getOutputStream());
                    outToClient.writeUTF(response);
                    String[] responses = serverJson.parseResponse(response);
                    if ((responses[0]).equals("warn")) logger.warn(responses[1] + " Your request: " + request);
                    else if ((responses[0]).equals("info"))
                        logger.info(responses[1] + " Your request: " + request+ "  new initial balance: " +((depositsHashMap.get(id)).getInitialBalance()).toString());
                        //logger.info(responses[1] + " Your request: " + request+ "  new initial balance: " +responses[2]);
                }

            } catch (IOException e) {
                if (isStopped()) {
                    System.out.println("Server Stopped.");
                    break;
                }
                throw new RuntimeException(
                        "Error accepting client connection", e);
            }
            this.threadPool.execute(
                    new WorkerRunnable(clientSocket,
                            "Thread Pooled Server"));
        }
        this.threadPool.shutdown();
        System.out.println("Server Stopped.");
    }


    private synchronized boolean isStopped() {
        return this.isStopped;
    }

    public synchronized void stop() {
        this.isStopped = true;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
    }

    private void openServerSocket() {
        try {
            this.serverSocket = new ServerSocket(this.serverPort);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port " + serverPort, e);
        }
    }
}
