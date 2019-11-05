package com.project;

import com.project.server.MasterServer;

public class MasterServerMain {

    public static void main(String[] args) {
        MasterServer testServer = new MasterServer();
        testServer.startServer(34567);
    }
}
