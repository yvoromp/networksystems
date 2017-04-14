package com.nedap.university.UDPpackageStructure;


/**
 * Created by yvo.romp on 11/04/2017.
 */
public class packageCreator {

    private int SOURCEPORT = 5555;


    private UDPheader UDPheader;

    public byte[] packageCreator(UDPheader UDPheader, byte[] dataToSend){
        byte[] byteArrayToSend = UDPheader.arrayToBytes();
        return mergeDataAndHeader(byteArrayToSend,dataToSend);
    }

    public byte[] mergeDataAndHeader(byte[] header, byte[] data){
        int hLen = header.length;
        int dLen = data.length;

        byte[] totalArrayToSend = new byte[hLen + dLen];
        System.arraycopy(header,0,totalArrayToSend,0,hLen);
        System.arraycopy(data,0,totalArrayToSend,hLen,dLen);
        return totalArrayToSend;
    }

}
