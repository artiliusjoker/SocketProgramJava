package com.project.protocols;

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
}
