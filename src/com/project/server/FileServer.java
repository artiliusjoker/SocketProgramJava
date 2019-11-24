package com.project.server;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Objects;

import com.project.fileslist.FileList;
import com.project.protocols.udp.Sender;

public class FileServer implements Server{
    public void startServer(int initPort){
        // find port for listening client handshake
        ServerSocket socketListener;
        int portListening = initPort;
        while(true) {
            try {
                socketListener = new ServerSocket(portListening);
                break;
            } catch (SocketException err) {
                portListening++;
            }
            catch (IOException err){
                System.err.println("Sorry there is an error in creating listener, please reboot server");
                System.exit(1);
            }
        }
        // Get user input for Master server info
        BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));
        String masterIp;
        int masterPort;
        try {
            System.out.print("Input master server IP address : ");
            masterIp = consoleInput.readLine();
            System.out.print("Input master server port : ");
            masterPort = Integer.parseInt(consoleInput.readLine());
        }
        catch (IOException err){
            err.printStackTrace();
            return;
        }

        // connect and send files list to Master
        connectMasterServer(masterIp, masterPort, portListening);
        System.out.println("Give Master server file list successfully");
        System.out.println("File server started listening successfully on port : " + portListening);

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

    private static void connectMasterServer(String hostIP, int masterPort, int listeningPort){
        Socket clientSocket = null;
        try {
            clientSocket = new Socket(hostIP, masterPort);
        } catch (Exception e) {
            System.err.println("Cannot connect to the server, try again later.");
            System.exit(1);
        }

        try {
            PrintStream os = new PrintStream(clientSocket.getOutputStream());
            os.println("File_Server");
            FileList sendList = makeFileList(clientSocket.getLocalAddress().getHostAddress(), listeningPort);
            FileList.sendFileList(sendList, clientSocket);
            sendList = null;
            os.println("end");
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

    private static FileList makeFileList(String hostIp, int port) throws IOException{
        FileList newList = new FileList(hostIp, port);
        String currPath = System.getProperty("user.dir");
        final File currDir = new File(currPath);
        try{
            for(final File file : Objects.requireNonNull(currDir.listFiles())){
                String fileName = file.getName();
                if(!fileName.equals("fileserver.jar") && !fileName.equals("files.txt")){
                    newList.addFiles(fileName);
                }
            }
        }
        catch (Exception err){
            throw new IOException("Cannot make file list, try again");
        }
        return newList;
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

                // Take file name and client listening port
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
                        // send the file
                        Sender sender = new Sender(clientAddr.getHostAddress(), clientPort, fileName);
                        sender.start();
                        System.out.println("Send successfully !");
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

    }
}
