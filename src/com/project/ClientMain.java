// Driver class
package com.project;
import com.project.client.Client;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ClientMain {
    public static void main(String[] args) throws IOException {
        // start client
        Client client = new Client();
        client.start();
    }
}
