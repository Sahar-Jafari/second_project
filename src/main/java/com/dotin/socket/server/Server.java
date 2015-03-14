package com.dotin.socket.server;


import com.dotin.socket.server.model.bl.ServerJson;
import com.dotin.socket.server.model.bl.ThreadPooledServer;

import java.io.File;

public class Server {

    public Server() {
        //LogWriter logFile;

        try {
            ServerJson json = new ServerJson();
            json.readJsonFile();

            File logFile= new File("./src/main/java/com/dotin/socket/server/resources/term21374.log");
            if (logFile.exists()) {
                if(logFile.delete()) System.out.println(logFile+ " has been deleted!");
            }

           System.out.println("Server Started!");

            ThreadPooledServer server = new ThreadPooledServer((json.getPortNumber()).intValue(),json.getDepositsHashMap());
            new Thread(server).start();

           //dummy, for stopping server
            /*try {
                Thread.sleep(20 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Stopping Server");
            server.stop();
*/
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Server server=new Server();
    }


}

