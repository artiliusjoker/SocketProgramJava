package com.project;
import com.project.client.Client;

import java.io.IOException;

public class ClientMain {
    public static void main(String[] args) throws IOException {
        Client testClient = new Client();
        testClient.connectMasterServer("192.168.1.11", 34567);
        testClient.readFileList();
    }
}
