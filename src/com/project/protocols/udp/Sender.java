package com.project.protocols.udp;

import com.project.protocols.Constant;
import com.project.protocols.udp.packet.MyPacket;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.*;
import java.util.Arrays;

public class Sender {
    private String host;
    private int port;
    private String fileSendName;

    public Sender(String clientIp, int clientPort, String fileName){
        this.host = clientIp;
        this.port = clientPort;
        this.fileSendName = fileName;
    }
    public void start() throws IOException {
        // Get started, define and initialize variables
        SocketAddress clientAddress = new InetSocketAddress(host, port);
        DatagramSocket socket = new DatagramSocket();
        RandomAccessFile inStream = new RandomAccessFile(new File(fileSendName), "r");
        int numChunks = (int) Math.ceil(1F * inStream.length() / Constant.MAX_CHUNK_SIZE);
        // Initialize to be all false, if receive ack for a seqNum then received[seqNum] is true
        boolean[] received = new boolean[numChunks + 1];

        // Create a new thread to listen Ack response
        Runnable runnable = new AckListener(numChunks, received, socket);
        Thread listener = new Thread(runnable);
        listener.start();

        // Main thread sending file
        // First send file length
        try{
            MyPacket infoPacket = MyPacket.infoPacket(inStream.length());
            while (!received[0])
            {
                DatagramPacket packet = new DatagramPacket(infoPacket.toBytes(), infoPacket.length(), clientAddress);
                socket.send(packet);
            }
        }
        catch (SocketException err){
            System.err.println("Fatal error in sending file length");
            err.printStackTrace();
            throw new IOException("Try again !");
        }

        // send chunks
        try {
            sendData(numChunks, received, socket, clientAddress, inStream);
        }
        catch (IOException err){
            System.err.println("Cannot send data !");
            err.printStackTrace();
            throw new IOException("Try again !");
        }

        // clean up
        socket.close();
        inStream.close();
    }

    // send chunks of file
    private void sendData(int numChunks, boolean[] check, DatagramSocket socket, SocketAddress clientAddr, RandomAccessFile inStream)
            throws IOException
    {
        byte[] buffer = new byte[Constant.MAX_CHUNK_SIZE];
        boolean finished = false;
        MyPacket packet;
        DatagramPacket udpPacket;

        while (!finished)
        {
            finished = true;
            for (int i = 1; i < numChunks + 1; i++){
                if(check[i]) continue; // has ack for this chunk
                finished = false; // if don't have ack, send chunk again
                inStream.seek((i - 1) * Constant.MAX_CHUNK_SIZE);
                inStream.read(buffer, 0, Constant.MAX_CHUNK_SIZE);
                packet = new MyPacket(i, buffer, Constant.DATA);
                udpPacket = new DatagramPacket(packet.toBytes(), packet.length(), clientAddr);
                socket.send(udpPacket);
            }
        }
    }

    private static class AckListener implements Runnable {
        private final int numChunks;
        private final boolean[] received;
        private DatagramSocket fileSocket;

        private AckListener(int numChunks, boolean[] received, DatagramSocket socket){
            this.fileSocket = socket;
            this.numChunks = numChunks;
            this.received = received;
        }

        @Override
        public void run(){
            MyPacket ackPacket;
            int chunksCount = 0;
            byte[] buffer = new byte[Constant.PACKET_HEADER_SIZE];
            DatagramPacket incomingAck = new DatagramPacket(buffer, Constant.PACKET_HEADER_SIZE);
            while (chunksCount <= this.numChunks)
            {
                try {
                    fileSocket.receive(incomingAck);
                }
                catch (IOException err){
                    err.printStackTrace();
                }
                ackPacket = new MyPacket(incomingAck);
                // sequence number = -1 => end protocol
                if (ackPacket.isValid() && ackPacket.getSeqNum() == -1) {
                    Arrays.fill(received, true);
                    return;
                }
                // else continue listening and set true
                if (ackPacket.isValid() && !received[ackPacket.getSeqNum()]) {
                    received[ackPacket.getSeqNum()] = true;
                    chunksCount ++;
                }
            }
        }
    }
}
