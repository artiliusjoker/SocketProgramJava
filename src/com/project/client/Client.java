package com.project.client;
import java.io.*;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;
import com.project.protocols.udp.Receiver;

public class Client {
    private final static int BASE_PORT = 30000;

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

    public void connectFileServer(String fileName, String hostIP, int port) throws IOException{
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
            }
            else throw new IOException("This server don't have that file");
        }
        catch (IOException err)
        {
            clientSocket.close();
            System.err.println(err.getMessage());
            throw new IOException("Cannot handshake");
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
