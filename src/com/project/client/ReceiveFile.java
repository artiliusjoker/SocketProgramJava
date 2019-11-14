package com.project.client;

import java.io.*;
import java.net.Socket;

public class ReceiveFile {
    public static void receiveFileList(String storedFile, Socket clientSocket, boolean mode){
        try {
            // Writer to store data to file fileListForClient.txt
            BufferedWriter writerToStoreFile = new BufferedWriter(new FileWriter(storedFile, mode));
            // Reader to read input stream from server ( server send list of files)
            BufferedReader masterServerInputStream = new
                    BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String bufferStr;
            String fileServerAddress = "\nHost server : " + clientSocket.getInetAddress().getHostAddress()
                                + clientSocket.getPort() + "\n";
            writerToStoreFile.write(fileServerAddress);
            while ((bufferStr = masterServerInputStream.readLine()) != null){
                if("end protocol".equals(bufferStr)) break;
                writerToStoreFile.write(bufferStr + "\n");
            }
            writerToStoreFile.close();
            masterServerInputStream.close();
            clientSocket.close();
        }
        catch (IOException e){
            System.out.println("Cannot get file list !!!");
            System.exit(1);
        }
        finally {
            System.out.println("Get list file successfully");
        }
    }
}
