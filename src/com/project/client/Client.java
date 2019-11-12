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
            os.println("end");
        } catch (Exception e) {
            System.err.println("Cannot get file from master server, try again.");
        }
        finally {
            os.close();
            clientSocket.close();
        }
    }

    public void connectFileServer(String hostIP, int port){
        Socket clientSocket = null;
        try {
            clientSocket = new Socket(hostIP, port);
        } catch (Exception e) {
            System.err.println("Cannot connect to the server, try again later.");
            System.exit(1);
        }
        try {
            if (fileServerHandshake(clientSocket, "abc.txt"))
            {
                System.out.println("Success");
            }
        }
        catch (IOException err)
        {
            System.out.println("Catch error in handshaking");
            System.exit(1);
        }
    }

    private static boolean fileServerHandshake(Socket socket, String fileName) throws IOException{
        PrintStream outStream = new PrintStream(socket.getOutputStream());
        BufferedReader inStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        boolean flag = false;
        try {
            String message;
            outStream.println(fileName);
            message = inStream.readLine();
            if(message.equals("ready")) flag = true;
            else throw new IOException("File server not ready !");
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        finally {
            outStream.close();
            inStream.close();
            socket.close();
        }
        return flag;
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
