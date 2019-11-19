package com.project.protocols.udp;

import com.project.protocols.Constant;
import com.project.protocols.udp.packet.MyPacket;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.SocketException;

public class Receiver {
    private SocketAddress sender;
    private DatagramSocket clientSocket;

    public Receiver(DatagramSocket socket){
        this.clientSocket = socket;
    }

    public void start(String fileName) throws IOException{
        // Important variables
        boolean[] checkACK;
        byte[] buffer = new byte[Constant.PACKET_MAX_SIZE];
        MyPacket segment;
        DatagramPacket packetReceive = new DatagramPacket(buffer, Constant.PACKET_MAX_SIZE);
        File file;
        RandomAccessFile writeStream = null;
        int numChunks = 0;
        int writeIndex = 0;
        long fileSize = 0;

        // While loop to receive file length
        try{
            while (true){
                clientSocket.receive(packetReceive);
                segment = new MyPacket(packetReceive);
                if(segment.getType() == Constant.META_DATA && segment.isValid())
                {
                    fileSize = segment.getFileSize();
                    numChunks = (int) Math.ceil(1F * fileSize / Constant.MAX_CHUNK_SIZE);
                    sender = packetReceive.getSocketAddress();
                    sendAck(segment.getSeqNum());
                    break;
                }
            }
        }
        catch (IOException err){
            System.err.println(err.getMessage());
            err.printStackTrace();
        }
        // set up window checker
        checkACK = new boolean[numChunks];

        // set up file writer
        try{
            file = new File(fileName);
            file.getAbsoluteFile().getParentFile().mkdirs();
            writeStream = new RandomAccessFile(file, "rw");
        }catch (FileNotFoundException err)
        {
            System.err.println("Cannot open file to write !");
            err.printStackTrace();
        }
        // receive file
        try {
            // while loop to receive data packets
            while (numChunks > 0){
                clientSocket.receive(packetReceive);
                segment = new MyPacket(packetReceive);
                if(segment.isValid()){
                    if(segment.getType() == Constant.DATA){
                        writeIndex = segment.getSeqNum() - 1;
                        if(!checkACK[writeIndex]){
                            writeStream.seek(writeIndex * Constant.MAX_CHUNK_SIZE);
                            writeStream.write(segment.getPacketData(), 0, segment.getDataSize());
                            checkACK[writeIndex] = true;
                            numChunks--;
                        }
                    }
                    sendAck(segment.getSeqNum());
                }
            }
            // file is received and stop protocol by sending -1
            writeStream.setLength(fileSize);
            for (int j = 0; j < 10; j++){
                sendAck(-1);
            }
        }
        catch (IOException err)
        {
            err.printStackTrace();
        }
        finally {
            if(writeStream != null) writeStream.close();
            clientSocket.close();
        }
    }

    private void sendAck(int sequenceNumber) throws IOException {
        MyPacket ack = new MyPacket(sequenceNumber, new byte[0], Constant.ACK);
        DatagramPacket ackPacket = new DatagramPacket(ack.toBytes(), ack.length(), sender);
        clientSocket.send(ackPacket);
    }

    public void stop(){
            if(clientSocket != null)
                clientSocket.close();
    }
}
