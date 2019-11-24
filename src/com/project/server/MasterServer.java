package com.project.server;
import java.io.*;
import java.net.*;

import com.project.fileslist.FileList;
import com.project.protocols.tcp.Tcp;

public class MasterServer implements Server{
    private ServerSocket socketForListener;

    @Override
    public void startServer(int initPort){
        InetAddress inetAddress;
        try {
            inetAddress = InetAddress.getLocalHost();
        }
        catch (UnknownHostException err){
            System.err.println("Cannot get IP address !");
            return;
        }
        // Get unused port
        while (true){
            try {
                socketForListener = new ServerSocket(initPort);
                System.out.println("Server started successfully.");
                System.out.println("Please connect : " + inetAddress.getHostAddress() + " port : " + initPort);
                break;
            }
            catch (SocketException err){
                initPort++;
            }
            catch (IOException err){
                System.err.println("Please reboot server !");
                return;
            }
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
                        serveClient();
                    }
                    else {
                        if(!"end".equals(typeOfClient)){
                        System.out.println("Handshake successfully with a file server !");
                        serveFileServer();
                        }
                    }
                    System.out.println("Serve successfully, waiting for new clients,...");
                    stop();
                    clientInputStream.close();
                }
                else throw new IOException("Cannot decide which type of clients");
            }
            catch (IOException err){
                System.err.println(err.getMessage());
                System.exit(1);
            }
        }

        private void serveFileServer(){
            try {
                FileList newList = FileList.receiveFileList(socketForClients);
                if(newList == null) throw new IOException("Fatal, cannot get list of files !");
                FileList.updateFileList(newList);
                System.out.println("Get file list from file server done !");
            }
            catch (IOException e) {
                System.out.println("Cannot get file list from file server !!!");
                e.printStackTrace();
                System.exit(1);
            }
        }

        private void serveClient(){
            try{
                Tcp.sendFile("master.bin", socketForClients);
            }
            catch (IOException err){
                err.printStackTrace();
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

