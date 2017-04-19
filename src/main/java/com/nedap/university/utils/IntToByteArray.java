package com.nedap.university.utils;

/**
 * Created by yvo.romp on 14/04/2017.
 */
public class IntToByteArray {

    public byte[] changeIntToByteArray(int intValue){
        byte [] totalByteArray = new byte[4];
        totalByteArray[3] = (byte) (intValue & 0xFF);
        totalByteArray[2] = (byte) ((intValue >> 8) & 0xFF);
        totalByteArray[1] = (byte) ((intValue >> 16) & 0xFF);
        totalByteArray[0] = (byte) ((intValue >> 24) & 0xFF);
        return totalByteArray;
    }




    public byte[] changeIntegerArrayToByteArray(Integer[] integerArray){
        byte[] packetContents = new byte[integerArray.length];
        for (int i = 0; i < integerArray.length; i++) {
            packetContents[i] = (byte) ((integerArray[i] & 0x000000ff));
        }
        return packetContents;
    }
}

