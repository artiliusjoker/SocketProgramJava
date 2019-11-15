package com.project.protocols.udp.packet;

import java.io.Serializable;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class MyPacket implements Serializable{
    private static final int MAX_SIZE = 1012;
    private static final int HEADER_SIZE = 12;
    // header info
    private int type;
    private int seqNum;
    private int packetSize;
    // data of packet
    private byte[] packetData;

    MyPacket(int type, int size, int seqNum, byte[] data) throws Exception {
        if (size > MAX_SIZE) {
            throw new Exception("Excess packet max size (1012 bytes)");
        }
        this.type = type;
        this.packetSize = size;
        this.seqNum = seqNum;
        this.packetData = data;
    }
    public static MyPacket createAck(int sequence, int type) throws Exception{
        return new MyPacket(type, HEADER_SIZE, sequence, new byte[0]);
    }
    public int getSeqNum() {
        return seqNum;
    }

    public long getPacketSize() {
        return packetSize;
    }

    public byte[] getPacketData() {
        return packetData;
    }

    public int getType(){
        return type;
    }

    public byte[] toBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(packetSize);
        buffer.putInt(type);
        buffer.putInt(packetSize);
        buffer.putInt(seqNum);
        buffer.put(packetData, 0, packetSize - HEADER_SIZE);
        return buffer.array();
    }

    public static MyPacket createPacket(byte[] bytes) throws Exception {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        int type = buffer.getInt();
        int length = buffer.getInt();
        int seqNum = buffer.getInt();
        if (length > HEADER_SIZE) {
            byte[] data = new byte[length - HEADER_SIZE];
            buffer.get(data, 0, length - HEADER_SIZE);
            return new MyPacket(type, length, seqNum, data);
        } else {
            return new MyPacket(type, length, seqNum, new byte[0]);
        }
    }

    public void send(String address, int port, DatagramSocket socket) {
        try {
            DatagramPacket sendPacket = new DatagramPacket(this.toBytes(),
                    this.packetSize, InetAddress.getByName(address), port);
            socket.send(sendPacket);
        } catch (Exception e) {
            System.out.println("Exception when sending packet");
        }
    }

}
