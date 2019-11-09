package com.project.packet;

import java.io.Serializable;

public class MyPacket implements Serializable{
    private String fileName;
    private int seqNum;
    private int ackNum;
    private byte[] packetData;
    private long packetSize;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getSeqNum() {
        return seqNum;
    }

    public void setSeqNum(int seqNum) {
        this.seqNum = seqNum;
    }

    public int getAckNum() {
        return ackNum;
    }

    public void setAckNum(int ackNum) {
        this.ackNum = ackNum;
    }

    public long getPacketSize() {
        return packetSize;
    }

    public void setPacketSize(long packetSize) {
        this.packetSize = packetSize;
    }

    public byte[] getPacketData() {
        return packetData;
    }

    public void setPacketData(byte[] packetData) {
        this.packetData = packetData;
    }
}
