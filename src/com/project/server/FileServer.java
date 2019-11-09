package com.project.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class FileServer implements Server{
    public void startServer(int initPort){
        //connectMasterServer("127.0.0.1", 34567);
        //System.out.println("Give Master server file list successfully");
        ServerSocket socketListener = null;
        try {
            socketListener = new ServerSocket(initPort);
            System.out.println("File server started listening successfully.");
        }catch (IOException err)
        {
            System.err.println("Sorry there is an error in creating listener, please reboot server");
            System.exit(1);
        }
        while (true)
        {
            try {
                Socket clientSocket = socketListener.accept();
                Runnable runnable = new FileHandleClients(clientSocket);
                Thread newClients = new Thread(runnable);
                newClients.start();
            }
            catch (IOException err)
            {
                System.err.println("Error in connection attempt with client.");
            }
        }
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

    private static class FileHandleClients implements Runnable{
        private Socket handshakeSocket;
        private FileHandleClients(Socket handshake)
        {
            this.handshakeSocket = handshake;
        }
        @Override
        public void run(){
            // Handshaking and checking file exist part
            String fileName;
            try {
                BufferedReader clientInputStream = new BufferedReader
                        (new InputStreamReader(handshakeSocket.getInputStream()));
                PrintStream clientOutStream = new PrintStream(handshakeSocket.getOutputStream());
                fileName = clientInputStream.readLine();
                if (fileName != null){
                    if(checkFileExist(fileName))
                    {
                        clientOutStream.println("ready");
                    }
                    else {
                        clientOutStream.println("sorry");
                        throw new IOException("File not exists, choose another file");
                    }
                }
                else throw new IOException("Cannot handshake with client, exiting...");
            }
            catch (IOException ex){
                System.err.println(ex.getMessage());
                System.exit(1);
            }
            finally {
                stopHandshake();
                System.out.println("Handshake and check file exist successfully, sending it for client,...");
            }
            // Sending file part
            //sendFileToClients(fileName);
        }

        private static boolean checkFileExist(String fileName)
        {
            File testOpen = new File(fileName);
            return testOpen.canRead();
        }
        private void stopHandshake()
        {
            try{
                if(handshakeSocket != null) handshakeSocket.close();
            }
            catch (IOException err)
            {
                System.out.println("Fatal error, can not close socket !");
            }
        }
        private static void sendFileToClients(String fileName){

        }
    }
}
