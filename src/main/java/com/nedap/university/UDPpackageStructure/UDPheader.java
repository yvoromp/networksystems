package com.nedap.university.UDPpackageStructure;

/**
 * Created by yvo.romp on 10/04/2017.
 */

import java.util.Arrays;

/**
 * header construction
 * | sourcePort | destPort | hLength | flags | checksum |
 */
public class UDPheader {

    private int sourcePort;
    private int destPort;
    private int hLength;
    private int flags;
    private int checksum;

    public byte[] totalBytesArray;


    public UDPheader(){
    }

    public UDPheader(int sourcePort, int destPort, int length, int flags, int checksum){
        this.sourcePort = sourcePort;
        this.destPort = destPort;
        this.hLength = length;
        this.flags = flags;
        this.checksum = checksum;
    }


    public byte[] arrayToBytes() {
        byte[] spBytes = changeIntToByteArray(sourcePort);
        byte[] dpBytes = changeIntToByteArray(destPort);
        byte[] lenBytes = changeIntToByteArray(hLength);
        byte[] flagBytes = changeIntToByteArray(flags);
        byte[] csBytes = changeIntToByteArray(checksum);

        return combineHeaderParts(spBytes, dpBytes, lenBytes, flagBytes, csBytes);
    }

        //changes int values and returns a byte array
    public byte[] changeIntToByteArray(int intValue){
        byte [] totalByteArray = new byte[4];
        totalByteArray[3] = (byte) (intValue & 0xFF);
        totalByteArray[2] = (byte) ((intValue >> 8) & 0xFF);
        totalByteArray[1] = (byte) ((intValue >> 16) & 0xFF);
        totalByteArray[0] = (byte) ((intValue >> 24) & 0xFF);
        totalBytesArray = totalByteArray;
        return totalBytesArray;
    }

    //combine the arrays of each header part to form the header
    public byte[] combineHeaderParts(byte[] spBytes, byte[] dpBytes, byte[] lenBytes, byte[] flagBytes, byte[] csBytes){
        int spLen = spBytes.length;
        int dpLen = dpBytes.length;
        int lLen = lenBytes.length;
        int fLen = flagBytes.length;
        int csLen = csBytes.length;

        byte[] headerToSend = new byte[spLen+dpLen+lLen+fLen+csLen];
        System.arraycopy(spBytes,0,headerToSend,0,spLen);
        System.arraycopy(dpBytes,0,headerToSend,spLen,dpLen);
        System.arraycopy(lenBytes,0,headerToSend,dpLen+spLen,lLen);
        System.arraycopy(flagBytes,0,headerToSend,dpLen+spLen+lLen,fLen);
        System.arraycopy(csBytes,0,headerToSend,dpLen+spLen+lLen+fLen,csLen);
        return headerToSend;
    }

    public String bytesToHex(byte[] in) {
        final StringBuilder builder = new StringBuilder();
        for(byte b : in) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }

}
