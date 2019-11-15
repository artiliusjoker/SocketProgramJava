package com.project.protocols.tcp;

import java.io.*;
import java.net.Socket;

public class SendReceiveTCP {
    public static void receiveFileText(String storedFile, Socket clientSocket, boolean mode){
        try {
            // Writer to store data to file fileListForClient.txt
            BufferedWriter writerToStoreFile = new BufferedWriter(new FileWriter(storedFile, mode));
            // Reader to read input stream from server ( server send list of files)
            BufferedReader masterServerInputStream = new
                    BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String bufferStr;
            String fileServerAddress = "\nHost server : " + clientSocket.getInetAddress().getHostAddress()
                    + "\n";
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
    public static void sendFileText(String fileName, Socket clientSocket){
        BufferedReader fileReader;
        PrintWriter outStreamToClient = null;
        try {
            outStreamToClient = new PrintWriter(clientSocket.getOutputStream(), true);
        }catch (IOException e){
            System.out.println("Error in creating output stream to client !");
        }
        try {
            // Open info file which stores file's information (host server's ip and port)
            File file = new File(fileName);
            System.out.println("Read stored file successfully !");

            // input file stream
            fileReader = new BufferedReader(new FileReader(file));

            // read file and push to socket output stream to client
            String strBuffer;
            while((strBuffer = fileReader.readLine()) != null) {
                outStreamToClient.println(strBuffer);
            }
            outStreamToClient.println("end protocol");
        }
        catch (AssertionError e){
            System.out.println("Cannot find stored text file !");
        }
        catch (IOException e){
            System.out.println("Error in reading stored text file !");
        }
        finally {
            System.out.println("Serve client successfully with text file !");
        }
    }
}
