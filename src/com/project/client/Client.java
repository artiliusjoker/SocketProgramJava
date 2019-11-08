package com.project.client;
import java.io.*;
import java.net.*;

public class Client {

    public void connectMasterServer(String hostIP, int port) throws IOException {
        Socket clientSocket = null;
        try {
           clientSocket = new Socket(hostIP, port);
        } catch (Exception e) {
            System.err.println("Cannot connect to the server, try again later.");
            System.exit(1);
        }

        PrintStream os = new PrintStream(clientSocket.getOutputStream());
        try {
            os.println("Client handshake");
            receiveFileList("fileListForClient.txt", clientSocket);
            //ReceiveFile.receiveFileList("fileListForClient.txt", clientSocket);
            os.println("dummy");
        } catch (Exception e) {
            System.err.println("Cannot get file from master server, try again.");
        }
        finally {
            os.close();
            clientSocket.close();
        }
    }

    public void connectFileServer(String hostIP, int port) throws IOException{

    }

    public void downloadFile(String filename){

    }

    public void readFileList(){
        try {
            File file = new File("fileListForClient.txt");
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String tempStr;
            while ((tempStr = bufferedReader.readLine()) != null){
                System.out.println(tempStr);
            }
            bufferedReader.close();
        }
        catch (FileNotFoundException e){
            System.exit(1);
            System.out.println("File not found !!!");
        }
        catch (IOException e){
            System.out.println("Fatal error, exiting... !!!");
            System.exit(1);
        }
    }

    private static void receiveFileList(String storedFile, Socket clientSocket) throws IOException{
        try {
            // Writer to store data to file fileListForClient.txt
            BufferedWriter writerToStoreFile = new BufferedWriter(new FileWriter(storedFile));
            // Reader to read input stream from server ( server send list of files)
            BufferedReader masterServerInputStream = new
                    BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String bufferStr;
            while ((bufferStr = masterServerInputStream.readLine()) != null){
                if("end protocol".equals(bufferStr)) break;
                writerToStoreFile.write(bufferStr + "\n");
            }
            writerToStoreFile.close();
            masterServerInputStream.close();
        }
        catch (IOException e){
            System.out.println("Cannot get file list from server !!!");
            System.exit(1);
        }
        finally {
            System.out.println("Get list file successfully");
        }
    }
}
