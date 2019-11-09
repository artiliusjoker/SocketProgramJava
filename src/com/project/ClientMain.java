package com.project;
import com.project.client.Client;

import java.io.IOException;

public class ClientMain {
    public static void main(String[] args) throws IOException {
        Client testClient = new Client();
        //testClient.connectMasterServer("127.0.0.1", 34567);
        //testClient.readFileList();
        testClient.connectFileServer("127.0.0.1", 36000);
    }
}
