package com.project.server;
import java.io.*;
import java.net.*;
import com.project.server.SendFile;
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
                System.out.println("Accepted connection : " + socketForClients);
                // multiple threads implements with Runnable and can server many clients
                Runnable runnable = new HandleClients(socketForClients);
                Thread newClients = new Thread(runnable);
                newClients.start();
                System.out.println("Serve successfully, waiting for new clients,...");
                break;
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
        //private PrintWriter out;
        private BufferedReader clientInputStream;
        private HandleClients(Socket socket){
            this.socketForClients = socket;
        }
        @Override
        public void run(){
            try {
                clientInputStream = new BufferedReader(new InputStreamReader(socketForClients.getInputStream()));
                String typeOfClient;
                while ((typeOfClient = clientInputStream.readLine()) != null){
                    if ("Client handshake".equals(typeOfClient)){
                        System.out.println("Handshake successfully with a client !");
                        SendFile.sendTextFile("fileList.txt", socketForClients);
                        stop();
                    }
                    else {
                        System.out.println("Handshake successfully with a file server !");
                        serveFileServer();
                        stop();
                        break;
                    }
                }
            }
            catch (IOException ex){
                System.exit(1);
            }
        }
        private static void serveFileServer(){

        }
        private static void stop(){
            //socketForClients.close();
        }
    }
}

