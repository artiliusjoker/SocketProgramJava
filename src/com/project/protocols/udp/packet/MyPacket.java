package com.project.protocols.udp.packet;

import com.project.protocols.Constant;
import java.util.Arrays;
import java.nio.ByteBuffer;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class MyPacket{
    // header info 12 bytes
    // type 0 : metaData
    //      1 : Data
    //      2 : ack
    private int type;
    private int checkSum;
    private int seqNum;

    // data of packet
    private byte[] packetData;
    private long fileSize = 0;

    // Init 1
    public MyPacket(int sequenceNumber, byte[] data, int type) {
        this.seqNum = sequenceNumber;
        this.packetData = data;
        checkSum = -sequenceNumber;
        for (byte b : data) {
            checkSum -= b;
        }
    }

    // Init 2
    public MyPacket(DatagramPacket packet, int type) {
        byte[] data = packet.getData();
        ByteBuffer buffer = ByteBuffer.wrap(data);
        checkSum = buffer.getInt();
        seqNum = buffer.getInt();
        this.packetData = Arrays.copyOfRange(buffer.array(), Constant.PACKET_HEADER_SIZE, data.length);
        if (type == 0 && seqNum == 0 && isValid()) {
            fileSize = buffer.getLong();
        }
    }

    // Sum checker
    public boolean isValid() {
        int sum = checkSum;
        sum += seqNum;
        for (byte b : packetData) {
            sum += b;
        }
        return sum == 0;
    }

    public int getSeqNum() {
        return seqNum;
    }

    public byte[] getPacketData() {
        return packetData;
    }

    public byte[] toBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(packetData.length + Constant.PACKET_HEADER_SIZE);
        buffer.putInt(checkSum);
        buffer.putInt(seqNum);
        buffer.put(packetData, 0, packetData.length);
        return buffer.array();
    }

    public int length() {
        return packetData.length + Constant.PACKET_HEADER_SIZE;
    }
}
