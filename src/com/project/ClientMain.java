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
        testClient.connectMasterServer("127.0.0.1", 34567);
        testClient.readFileList();

        BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));
        // File and File master info input
        String fileName;
        String ipAdrr;
        int port;

        System.out.print("Input file name to download : ");
        fileName = consoleInput.readLine();

        System.out.print("Input IP to download : ");
        ipAdrr = consoleInput.readLine();

        System.out.print("Input port to download (handshaking port) : ");
        port = Integer.parseInt(consoleInput.readLine());

        // File server
        testClient.connectFileServer(fileName, ipAdrr, port);
    }
}
