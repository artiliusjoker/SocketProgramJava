package com.project.fileslist;

import java.io.*;
import java.util.ArrayList;

public class FileList implements Serializable{
    private String host;
    private int port;
    private ArrayList<String> fileNames;

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

    public static void writeToFile(ArrayList<FileList> myArray, String fileName) {
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

    public static void readFromFile(String fileName) throws FileNotFoundException {
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
            return;
        }
        catch (ClassNotFoundException err)
        {
            System.out.println("Catch class not found exception");
            err.printStackTrace();
            return;
        }
        for(FileList fileLists : buffer) {
            fileLists.showList();
            System.out.print('\n');
        }
    }
}
