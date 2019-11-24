package com.project.protocols.tcp;

import java.net.Socket;
import java.io.*;

public class Tcp {

    public static void sendFile(String fileName, Socket socket) throws IOException{
        File file = new File(fileName);

        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        FileInputStream fis = new FileInputStream(file);
        DataInputStream dis = new DataInputStream(new BufferedInputStream(fis));

        byte[] buffer = new byte[(int) file.length()];
        dis.readFully(buffer, 0 , buffer.length);

        dos.writeInt(buffer.length);
        dos.write(buffer, 0 , buffer.length);
        dos.flush();

        fis.close();
        dos.close();
        dis.close();
    }

    public static void receiveFile(String fileName, Socket socket) throws IOException{
        DataInputStream dis = new DataInputStream(socket.getInputStream());
        DataOutputStream dos = new DataOutputStream(new FileOutputStream(fileName));
        int bytesRead;
        byte[] buffer = new byte[1024];

        int fileSize = dis.readInt();
        while (fileSize > 0 && (bytesRead = dis.read(buffer, 0, (int) Math.min(buffer.length, fileSize))) != -1) {
            dos.write(buffer, 0, bytesRead);
            fileSize -= bytesRead;
        }
        dis.close();
        dos.close();
    }

}
