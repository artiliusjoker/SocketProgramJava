package com.project.protocols.udp.packet;

import com.project.protocols.Constant;
import java.util.Arrays;
import java.nio.ByteBuffer;
import java.net.DatagramPacket;

public class MyPacket{
    // header : 12 bytes
    // type 0 : metaData
    //      1 : Data
    //      2 : ack
    private int type;
    private int checkSum;
    private int seqNum;

    // data of packet
    private byte[] packetData;
    // file name is not sent
    private long fileSize = 0;

    // Constructor 1 : create packet from data (sender side) receiver (make ack packet)
    public MyPacket(int sequenceNumber, byte[] data, int type) {
        this.type = type;
        this.seqNum = sequenceNumber;
        this.packetData = data;
        this.checkSum = -sequenceNumber;
        for (byte b : data) {
            this.checkSum -= b;
        }
    }

    // Constructor 2 : create MyPacket from DatagramPacket ( receiver side)
    public MyPacket(DatagramPacket packet) {
        byte[] data = packet.getData();
        ByteBuffer buffer = ByteBuffer.wrap(data);
        this.checkSum = buffer.getInt();
        this.seqNum = buffer.getInt();
        this.type = buffer.getInt();
        this.packetData = Arrays.copyOfRange(buffer.array(), Constant.PACKET_HEADER_SIZE, data.length);
        if (this.type == Constant.META_DATA && this.seqNum == 0 && isValid()) {
            fileSize = buffer.getLong();
        }
    }

    // Constructor 3 : create info packet
    public static MyPacket infoPacket(long fileSize)
    {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putLong(fileSize);
        return new MyPacket(0, buffer.array(), Constant.META_DATA);
    }

    // Sum checker
    public boolean isValid() {
        int sum = checkSum;
        sum += seqNum;
        for (byte b : packetData) {
            sum += b;
        }
        return sum == 0 && (type>=0 && type <=2);
    }

    public int getType(){
        return type;
    }

    public int getSeqNum() {
        return seqNum;
    }

    public byte[] getPacketData() {
        return packetData;
    }

    public long getFileSize(){
        return fileSize;
    }

    public byte[] toBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(packetData.length + Constant.PACKET_HEADER_SIZE);
        buffer.putInt(checkSum);
        buffer.putInt(seqNum);
        buffer.putInt(type);
        buffer.put(packetData, 0, packetData.length);
        return buffer.array();
    }

    public int length() {
        return packetData.length + Constant.PACKET_HEADER_SIZE;
    }
}
