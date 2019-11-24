package com.project.protocols;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MyAddress {
    private String address;
    private int port;

    public MyAddress(String address, int port){
        this.address = address;
        this.port = port;
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public static MyAddress getUserInput(){
        // Get user input for Master server info
        BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));
        String masterIp;
        int masterPort;
        try {
            System.out.print("Input master server IP address : ");
            masterIp = consoleInput.readLine();
            System.out.print("Input master server port : ");
            masterPort = Integer.parseInt(consoleInput.readLine());
        }
        catch (IOException err){
            err.printStackTrace();
            return null;
        }
        return new MyAddress(masterIp, masterPort);
    }
}
