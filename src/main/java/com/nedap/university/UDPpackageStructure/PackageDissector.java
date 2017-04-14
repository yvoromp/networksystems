package com.nedap.university.UDPpackageStructure;

import java.net.DatagramPacket;
import java.nio.ByteBuffer;

/**
 * Created by yvo.romp on 12/04/2017.
 */
public class PackageDissector {

    private byte[] headerPart;
    private byte[] dataPart;

    //header dataparts
    private byte[] spPart;
    private byte[] dpPart;
    private byte[] lPart;
    private byte[] fPart;
    private byte[] csPart;

    //flags
    public boolean isBC = false;               //1 is activated
    public boolean isACK = false;             //10 is activated
    public boolean isNewPort = false;        //100 is activated
    public boolean isRequest = false;         //1000 is activated
    public boolean isReqAnswer = false;         //10000 is activated


    public PackageDissector(DatagramPacket packet){
        byte [] totalData = packet.getData();
        separateHeader(totalData);
        separateHeaderValues(headerPart);
        checkFlags(fPart);



    }
    public PackageDissector(){

    }


    public void disectPackage(){

    }

    private void separateHeader(byte[] totalData){
        byte[] headerPart = new byte[20];
        int dataLength = totalData.length-headerPart.length;
        byte[] dataPart = new byte[dataLength];
        System.arraycopy(totalData,0,headerPart,0,headerPart.length);
        System.arraycopy(totalData,headerPart.length,dataPart,0,dataPart.length);
        this.dataPart = dataPart;
        this.headerPart = headerPart;

    }

    private void separateHeaderValues(byte[] headerPart){
        spPart = new byte[4];
        dpPart = new byte[4];
        lPart = new byte[4];
        fPart = new byte[4];
        csPart = new byte[4];

        System.arraycopy(headerPart,0,spPart,0,4);
        System.arraycopy(headerPart,4,dpPart,0,4);
        System.arraycopy(headerPart,8,lPart,0,4);
        System.arraycopy(headerPart,12,fPart,0,4);
        System.arraycopy(headerPart,16,csPart,0,4);
    }


    public void checkFlags(byte[] fPart){
        int flagValue = ByteBuffer.wrap(fPart).getInt();
        if(flagValue/10000 == 1){
            System.out.println("received a reqAnswerflag");
            isReqAnswer = true;
        }
        flagValue %= 10000;
        if(flagValue / 1000 == 1){
            System.out.println("received a requestflag");
            isRequest = true;
        }
        flagValue %= 1000;
        if(flagValue / 100 == 1){
            System.out.println("received a newportflag");
            isNewPort = true;
        }
        flagValue %=100;
        if(flagValue / 10 == 1){
            System.out.println("received an ackflag ");
            isACK = true;
        }
        flagValue %= 10;
        if(flagValue / 1 == 1){
            System.out.println("received a bcflag");
            isBC = true;
        }
    }

    public void resetFlags(){
         isBC = false;
         isACK = false;
         isNewPort = false;
         isRequest = false;
         isReqAnswer = false;
    }

    public byte[] getDataPart() {
        return dataPart;
    }
}
