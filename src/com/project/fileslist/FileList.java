package com.project.fileslist;

import java.io.*;
import java.util.ArrayList;
import java.net.Socket;
import com.project.protocols.MyAddress;

public class FileList implements Serializable{
    private String host;
    private int port;
    private ArrayList<String> fileNames;

    private ArrayList<String> getFileNames() {
        return fileNames;
    }

    private int getPort() {
        return port;
    }

    private String getHost() {
        return host;
    }

    public FileList(String host, int port) {
        this.host = host;
        this.port = port;
        this.fileNames = new ArrayList<>();
    }

    public void addFiles(String fileName) {
        if(fileName != null) {
            this.fileNames.add(fileName);
        }
    }

    private void showList() {
        System.out.println(host);
        System.out.println(port);
        for(String fileName : fileNames) {
            System.out.println(fileName);
        }
    }

    private static void writeToFile(ArrayList<FileList> myArray, String fileName) {
        try {
            FileOutputStream fos = new FileOutputStream(fileName);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(myArray);
            oos.close();
            fos.close();
        }
        catch(IOException err) {
            err.printStackTrace();
        }
    }

    private static ArrayList<FileList> readFromFile(String fileName) throws FileNotFoundException {
        ArrayList<FileList> buffer = new ArrayList<>();
        FileInputStream fis = new FileInputStream(fileName);
        try {
            ObjectInputStream ois = new ObjectInputStream(fis);

            // Read fileList in FileLists
            Object bufferObj = ois.readObject();
            Iterable<?> bufferIte = (Iterable<?>) bufferObj;
            for(Object obj : bufferIte) {
                buffer.add((FileList) obj);
            }
            ois.close();
            fis.close();
        }
        catch (IOException err)
        {
            err.printStackTrace();
            return null;
        }
        catch (ClassNotFoundException err)
        {
            System.out.println("Catch class not found exception");
            err.printStackTrace();
            return null;
        }
        return buffer;
    }

    public static void showFileList(String fileName) throws IOException{
        ArrayList<FileList> fileLists = null;
        try{
            fileLists = FileList.readFromFile(fileName);
        }
        catch (FileNotFoundException err){
            err.printStackTrace();
            return;
        }

        if(fileLists == null) throw new IOException("Cannot read file list !");
        for(FileList fileList : fileLists) {
            fileList.showList();
            System.out.print('\n');
        }
        fileLists = null;
    }

    public static void updateFileList(FileList newList) throws IOException{
        File testOpen = new File("master.bin");
        ArrayList<FileList> fileLists = null;

        if(testOpen.canRead()){
            fileLists = FileList.readFromFile("master.bin");
        }
        else {
            fileLists = new ArrayList<>();
        }
        if(fileLists == null) throw new IOException("Fatal ! Try again service 2 !");
        fileLists.add(newList);
        FileList.writeToFile(fileLists, "master.bin");
        fileLists = null;
    }

    public static void sendFileList(FileList newList, Socket socket) throws IOException{
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        try {
            oos.writeObject(newList);
        }
        catch (IOException err){
            err.printStackTrace();
        }
    }

    public static FileList receiveFileList(Socket socket) throws IOException{
        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
        FileList fileList = null;
        try {
            // Read a fileList
            Object bufferObj = ois.readObject();
            fileList = (FileList) bufferObj;
        }
        catch (IOException err){
            err.printStackTrace();
            return null;
        }
        catch (ClassNotFoundException err)
        {
            System.out.println("Catch class not found exception");
            err.printStackTrace();
            return null;
        }
        return fileList;
    }

    public static MyAddress getAddress(String fileName) throws IOException{
        MyAddress address = null;
        ArrayList<FileList> fileLists = null;
        ArrayList<String> buffer = null;
        try{
            fileLists = FileList.readFromFile("client.bin");
        }
        catch (FileNotFoundException err){
            err.printStackTrace();
            return null;
        }

        if(fileLists == null) throw new IOException("Cannot read file list !");
        for(FileList fileList : fileLists) {
            buffer = fileList.getFileNames();
            if(buffer.indexOf(fileName) != -1){
                address = new MyAddress(fileList.getHost(), fileList.getPort());
                break;
            }
        }
        fileLists = null;
        return address;
    }
}
