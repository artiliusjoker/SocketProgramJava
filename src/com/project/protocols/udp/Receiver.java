package com.project.protocols.udp;

import com.project.protocols.Constant;
import com.project.protocols.udp.packet.MyPacket;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;

public class Receiver {
    private InetAddress senderAddr;
    private int senderPort;
    private DatagramSocket clientSocket;

    public Receiver(InetAddress address, int port, DatagramSocket socket){
        this.senderAddr = address;
        this.senderPort = port;
        this.clientSocket = socket;
    }

    public void start(String fileName){
        MyPacket segment;
        byte[] buffer = new byte[Constant.PACKET_MAX_SIZE];
        DatagramPacket packetReceive = new DatagramPacket(buffer, Constant.PACKET_MAX_SIZE);
        File file;
        RandomAccessFile writeStream;
        long fileSize;
        int numChunks;
        boolean[] checkACK;
        int writeIndex;

    }

    private void sendAck(int sequenceNumber) throws Exception {
        MyPacket ack = new MyPacket(sequenceNumber, new byte[0], Constant.ACK);
        DatagramPacket ackPacket = new DatagramPacket(ack.toBytes(), ack.length(), senderAddr, senderPort);
        clientSocket.send(ackPacket);
    }
}
