package com.project.client;
import java.io.*;
import java.net.*;
public class Client {
    private static Socket clientSocket;
    private static BufferedReader stdin;
    private static PrintStream os;
    //private static String fileDownloadName;
    public void connectMasterServer(String hostIP, int port) throws IOException {
        try {
            clientSocket = new Socket(hostIP, port);
            stdin = new BufferedReader(new InputStreamReader(System.in));
        } catch (Exception e) {
            System.err.println("Cannot connect to the server, try again later.");
            System.exit(1);
        }

        os = new PrintStream(clientSocket.getOutputStream());
        try {
            os.println("Client handshake");
            receiveFileList("fileListForClient.txt");
            os.println("dummy");
        } catch (Exception e) {
            System.err.println("not valid input");
        }
        finally {
            stdin.close();
            os.close();
            clientSocket.close();
        }
    }
    public void readFileList(){
        try {
            File file = new File("fileListForClient.txt");
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String tempStr;
            while ((tempStr = bufferedReader.readLine()) != null){
                System.out.println(tempStr);
            }
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
    public void connectFileServer(String hostIP, int port) throws IOException{

    }
    private static void receiveFileList(String storedFile) throws IOException{
        try {
            // create FileWriter objects to create BufferWriter objects for writing
            // string to file
            FileWriter fileToStore = new FileWriter(storedFile);
            BufferedWriter writerToStoreFile = new BufferedWriter(fileToStore);
            // Reader to read input stream from server ( server send list of files)
            BufferedReader masterServerInputStream = new
                    BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String bufferStr;
            while ((bufferStr = masterServerInputStream.readLine()) != null){
                if("end protocol".equals(bufferStr)) break;
                writerToStoreFile.write(bufferStr + "\n");
            }
            writerToStoreFile.close();
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
