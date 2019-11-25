package com.project.client;

import java.io.*;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;

import com.project.protocols.MyAddress;
import com.project.protocols.udp.Receiver;
import com.project.protocols.tcp.Tcp;
import com.project.fileslist.FileList;

public class Client {
    private final static int BASE_PORT = 30000;

    public void connectMasterServer() throws IOException {
        // Input master info
        MyAddress masterAddr = MyAddress.getUserInput();
        if(masterAddr == null) throw new IOException("Cannot get input, please try again !");

        // create socket with master server
        Socket clientSocket = null;
        try {
           clientSocket = new Socket(masterAddr.getAddress(), masterAddr.getPort());
        } catch (Exception e) {
            System.err.println("Cannot connect to the server, try again later.");
            System.exit(1);
        }

        // Handshake with master server
        PrintStream os = new PrintStream(clientSocket.getOutputStream());
        try {
            os.println("Client handshake");
            Tcp.receiveFile("client.bin", clientSocket);
            os.println("end");
        } catch (Exception e) {
            System.err.println("Cannot get file from master server, try again.");
        }
        finally {
            os.close();
            clientSocket.close();
        }
    }

    public void readFileList(){
        System.out.println("Files available to download :");
        try{
            FileList.showFileList("client.bin");
        }
        catch (IOException err){
            err.printStackTrace();
        }
    }

    public void downloadFile() throws IOException{
        BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));
        while (true){
            // File name to download
            //BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));
            String fileName;
            System.out.print("Input file name to download, input 'stop' to end program : ");
            fileName = consoleInput.readLine();
            if(fileName.equals("stop")){
                System.out.println("Good bye");
                break;
            }
            System.out.flush();

            // Create new thread to download
            Runnable runnable = new clientThread(fileName);
            Thread newThread = new Thread(runnable);
            newThread.start();

            // Wait thread to print screen. Otherwise it will be hard to input new args
            try {
                newThread.join();
            }
            catch (Exception err){
                err.printStackTrace();
            }
        }
    }

    private static class clientThread implements Runnable{
        private String fileName;
        private clientThread(String fileName){
            this.fileName = fileName;
        }
        @Override
        public void run(){
            // Get IP and port of File Server stored in file
            String hostIP;
            int port;
            try{
                MyAddress address = FileList.getAddress(fileName);
                if(address == null) return;
                hostIP = address.getAddress();
                port = address.getPort();
            }
            catch (IOException err){
                err.printStackTrace();
                return;
            }
            // create TCP socket for handshaking
            Socket clientSocket = null;
            try {
                clientSocket = new Socket(hostIP, port);
            } catch (Exception e) {
                System.err.println("Cannot connect to file server, try again later.");
                System.exit(1);
            }

            // find port for receiving file (UDP)
            int portListening = BASE_PORT;
            DatagramSocket socket;
            while(true) {
                try {
                    socket = new DatagramSocket(portListening);
                    break;
                } catch (SocketException e) {
                    portListening++;
                }
            }

            // handshaking in TCP
            try {
                if (fileServerHandshake(clientSocket, fileName, portListening))
                {
                    System.out.println("Success handshaking !");
                    clientSocket.close();
                    // Begin receive file
                    Receiver receiver = new Receiver(socket);
                    receiver.start(fileName);
                    receiver.stop();
                    System.out.println("Download successfully !");
                }
                else throw new IOException("Error handshaking with file server ! Try again !");
            }
            catch (IOException err)
            {
                System.err.println(err.getMessage());
                err.printStackTrace();
            }
            finally {
                try {
                    clientSocket.close();
                }
                catch (IOException err){
                    err.printStackTrace();
                }
            }
        }
    }

    private static boolean fileServerHandshake(Socket socket, String fileName, int portListening) throws IOException{
        PrintStream outStream = new PrintStream(socket.getOutputStream());
        BufferedReader inStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        boolean flag = false;
        try {
            String message;
            String fileNameAndPort = fileName + " " + portListening;
            outStream.println(fileNameAndPort);
            message = inStream.readLine();
            if(message.equals("ready")) flag = true;
            else throw new IOException("File server not ready !");
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        finally {
            outStream.close();
            inStream.close();
            socket.close();
        }
        return flag;
    }

}
