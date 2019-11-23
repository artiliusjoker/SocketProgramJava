package com.project;

import com.project.server.FileServer;

import java.io.IOException;

public class FileServerMain {
    public static void main(String[] args) {
        FileServer fileServer = new FileServer();
        try{
            FileServer.makeFileList();
        }
        catch (IOException err){
            System.err.println(err.getMessage());
            err.printStackTrace();
        }
        fileServer.startServer(30000);
    }
}
