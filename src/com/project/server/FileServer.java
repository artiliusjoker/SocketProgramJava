package com.project.server;

import java.io.*;
import java.net.Socket;
import com.project.server.SendFile;

public class FileServer implements Server{
    public void startServer(int initPort){
        connectMasterServer("127.0.0.1", 34567);
    }
    public void stopServer() throws IOException {

    }
    private static void connectMasterServer(String hostIP, int port){
        Socket clientSocket = null;
        try {
            clientSocket = new Socket(hostIP, port);
        } catch (Exception e) {
            System.err.println("Cannot connect to the server, try again later.");
            System.exit(1);
        }

        try {
            PrintStream os = new PrintStream(clientSocket.getOutputStream());
            os.println("File handshake");
            SendFile.sendTextFile("newfile.txt", clientSocket);
            os.println("Goodbye");
            os.close();
        } catch (IOException e) {
        System.err.println("Cannot send information to master server, try again.");
        }
    }

    private static class HandleClients implements Runnable{
        @Override
        public void run(){

        }
        private static void serveClients(){

        }
        private static void stop(){

        }
    }
}
