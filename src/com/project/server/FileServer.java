package com.project.server;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import com.project.protocols.udp.Sender;

public class FileServer implements Server{
    public void startServer(int initPort){
        connectMasterServer("127.0.0.1", 34567);
        System.out.println("Give Master server file list successfully");
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
        finally {
            try{
                clientSocket.close();
            }
            catch (IOException err){
                System.err.println("cannot close socket to master server !");
            }
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
            String readString;
            String[] bufferSplit;
            // Client info
            int clientPort; // port for transfer files, not for handshake
            InetAddress clientAddr;

            try {
                clientAddr = handshakeSocket.getInetAddress();
                // Handshake and get requested file name and client listening port
                BufferedReader clientInputStream = new BufferedReader
                        (new InputStreamReader(handshakeSocket.getInputStream()));
                PrintStream clientOutStream = new PrintStream(handshakeSocket.getOutputStream());
                readString = clientInputStream.readLine();
                bufferSplit = readString.split(" ", 2);
                fileName = bufferSplit[0];
                clientPort = Integer.parseInt(bufferSplit[1]);

                // Check file exist
                if (fileName != null){
                    if(checkFileExist(fileName))
                    {
                        clientOutStream.println("ready");
                        System.out.println("Handshake and check file exist successfully" +
                                            ", sending it for client,...");
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
            }
            finally {
                // close handshaking socket
                try {
                    handshakeSocket.close();
                }catch (IOException err){
                    err.printStackTrace();
                }
            }
        }

        private static boolean checkFileExist(String fileName)
        {
            File testOpen = new File(fileName);
            return testOpen.canRead();
        }

        private static void sendFileToClients(String fileName){

        }

    }
}
