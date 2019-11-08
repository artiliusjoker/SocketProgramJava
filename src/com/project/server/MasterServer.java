package com.project.server;
import java.io.*;
import java.net.*;
import com.project.client.ReceiveFile;
public class MasterServer implements Server{
    private ServerSocket socketForListener;

    @Override
    public void startServer(int initPort){
        try {
            socketForListener = new ServerSocket(initPort);
            System.out.println("Server started successfully.");
        } catch (Exception e) {
            System.err.println("Port already in use, please change to another port.");
            System.exit(1);
        }
        while (true) {
            try {
                Socket socketForClients = socketForListener.accept();
                System.out.println("Accepted connection : " + socketForClients.getInetAddress().getHostAddress());
                // multiple threads implements with Runnable and can server many clients
                Runnable runnable = new HandleClients(socketForClients);
                Thread newClients = new Thread(runnable);
                newClients.start();
            } catch (Exception e) {
                System.err.println("Error in connection attempt.");
            }
        }
    }
    @Override
    public void stopServer() throws IOException {
        socketForListener.close();
    }
    // inner class for serving 2 types of clients and multiple clients with Runnable
    private static class HandleClients implements Runnable{
        private Socket socketForClients;
        private HandleClients(Socket socket){
            this.socketForClients = socket;
        }

        @Override
        public void run(){
            try {
                BufferedReader clientInputStream = new BufferedReader(new InputStreamReader(socketForClients.getInputStream()));
                String typeOfClient;
                typeOfClient = clientInputStream.readLine();
                if (typeOfClient != null){
                    if ("Client handshake".equals(typeOfClient)){
                        System.out.println("Handshake successfully with a client !");
                        SendFile.sendTextFile("fileList.txt", socketForClients);
                    }
                    else {
                        System.out.println("Handshake successfully with a file server !");
                        serveFileServer();
                    }
                    System.out.println("Serve successfully, waiting for new clients,...");
                    stop();
                }
                else throw new IOException("Cannot decide which type of clients");
            }
            catch (IOException ex){
                System.err.println(ex.getMessage());
                System.exit(1);
            }
        }
        private void serveFileServer(){
            try {
                ReceiveFile.receiveFileList("fileList.txt", socketForClients, true);
            }
            catch (Exception e) {
                System.out.println("Cannot get file list from file server !!!");
                System.exit(1);
            }
        }
        private void stop(){
            try{
                if(socketForClients != null) socketForClients.close();
            }
            catch (IOException err)
            {
                System.out.println("Cannot close socket, fatal error !");
                System.exit(1);
            }
        }
    }
}

