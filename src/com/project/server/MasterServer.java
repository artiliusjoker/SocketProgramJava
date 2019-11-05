package com.project.server;
import java.io.*;
import java.net.*;

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
                        serveClient(socketForClients);
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
        private static void serveClient(Socket clientSoc)
        {
            final String fileName = "fileList.txt";
            BufferedReader fileReader;
            PrintWriter outStreamToClient = null;
            try {
                outStreamToClient = new PrintWriter(clientSoc.getOutputStream(), true);
            }catch (IOException e){
                System.out.println("Error in creating output stream to client !");
            }
            try {
                // Class loader to use getResourceAsStream to read file
                InputStream streamTemp = HandleClients.class.getClassLoader().getResourceAsStream(fileName);
                System.out.println("Read stored file successfully !");

                // input file stream
                assert streamTemp != null;
                fileReader = new BufferedReader(new InputStreamReader(streamTemp));

                // read file and push to socket output stream to client
                String strBuffer;
                while((strBuffer = fileReader.readLine()) != null) {
                    outStreamToClient.println(strBuffer);
                }
                outStreamToClient.println("end protocol");
                streamTemp.close();
            }
            catch (AssertionError e){
                System.out.println("Cannot find stored file !");
            }
            catch (IOException e){
                System.out.println("Error in reading stored file !");
            }
            finally {
                System.out.println("Serve client successfully !");
            }
        }
        private static void serveFileServer(){

        }
        private static void stop(){
            //socketForClients.close();
        }
    }
}

