package com.project.server;
import java.io.*;
import java.net.Socket;

public class SendFile {
    public static void sendTextFile(String fileName, Socket clientSocket){
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
