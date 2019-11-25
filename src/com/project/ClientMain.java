// Driver class
package com.project;
import com.project.client.Client;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ClientMain {
    public static void main(String[] args) throws IOException {
        // Master server
        Client testClient = new Client();
        testClient.connectMasterServer();
        testClient.readFileList();

        // File server
        testClient.downloadFile();
    }
}
