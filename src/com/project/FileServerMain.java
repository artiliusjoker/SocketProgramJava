package com.project;

import com.project.server.FileServer;

import java.io.IOException;

public class FileServerMain {
    public static void main(String[] args) {
        FileServer fileServer = new FileServer();
        fileServer.startServer(30000);
    }
}
