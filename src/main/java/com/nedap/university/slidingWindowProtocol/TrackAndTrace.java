package com.nedap.university.slidingWindowProtocol;

import com.nedap.university.utils.ByteToIntArray;
import com.nedap.university.utils.IntToByteArray;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by yvo.romp on 15/04/2017.
 */



public class TrackAndTrace {

    private SwProtocol swProtocol;
    private Integer[] fileContents;
    private Set<Integer> receivedAckSet;
    private Map<Integer, Boolean> ackGivenForPacket;
    private IntToByteArray intToByteArray = new IntToByteArray();
    private ByteToIntArray byteToIntArray = new ByteToIntArray();

    private static final int WINDOWSIZE = 250;
    private static final int DATASIZE = 500;
    private static final int HEADERSIZE = 1;
    private static final int NUMBEROFACKCHECKS = 50;

    private int packetCounter;
    private int lowerBound;
    private int upperBound;
    private int filePointer;

    private DatagramSocket downloadSocket;


    public void startTrackTraceAsServer(SwProtocol sWP, Set<Integer> receivedAckSet, int totalNumberOfPackets, int filePointer, Integer[] fileContent){
        swProtocol = sWP;
        fileContents = fileContent;
        ackGivenForPacket = new HashMap<>();
        this.receivedAckSet = receivedAckSet;
        while (receivedAckSet.size() != totalNumberOfPackets){
            //sets the lower and upperbound of the senderwindow
            if(receivedAckSet.isEmpty()){
                lowerBound = 1;
                upperBound = Math.min(WINDOWSIZE,totalNumberOfPackets);
            }else{
                lowerBound = setLowerBound(receivedAckSet);
                upperBound = setUpperBound(receivedAckSet,totalNumberOfPackets);
            }
            sendPacketsIfServer();
        }
    }

    public void startTrackTraceAsClient(SwProtocol sWP, Set<Integer> receivedAckSet, int totalNumberOfPackets, int filePointer, Integer[] fileContent){
        swProtocol = sWP;
        fileContents = fileContent;
        ackGivenForPacket = new HashMap<>();
        this.receivedAckSet = receivedAckSet;
        while (receivedAckSet.size() != totalNumberOfPackets){
            //sets the lower and upperbound of the senderwindow
            if(receivedAckSet.isEmpty()){
                lowerBound = 1;
                upperBound = WINDOWSIZE;
            }else{
                lowerBound = setLowerBound(receivedAckSet);
                upperBound = setUpperBound(receivedAckSet,totalNumberOfPackets);
            }
            sendPacketsIfClient();
        }
    }

    private int setLowerBound(Set<Integer> receivedAck){
        if (!receivedAck.isEmpty()) {
            for (int i = 1; i <= Collections.max(receivedAck); i++) {
                if (!receivedAck.contains(i)) {
                    return i;
                }
            }
            return Collections.max(receivedAck);
        }
        return 1;

    }

    private int setUpperBound(Set<Integer> receivedAck, int numberOfPackets){
        int upperBound = setLowerBound(receivedAck)+WINDOWSIZE;
        if(upperBound > numberOfPackets){
            upperBound = numberOfPackets;
        }
        return upperBound;
    }

    private void sendPacketsIfServer(){
        createSocket();
        swProtocol.getServerHandler().setDownloadSocket(downloadSocket);
        for(packetCounter = lowerBound; packetCounter <= upperBound ;packetCounter++){
            System.out.println("packetcounter:  " +packetCounter);
            //packet isn't acknowledged yet and (isn't added to the map or when added false)
            if(!receivedAckSet.contains(packetCounter) && (!ackGivenForPacket.containsKey(packetCounter) || ackGivenForPacket.get(packetCounter))){
                // send/resend packet
                filePointer = DATASIZE * (packetCounter -1) ;
                Integer[] pkt = createSwPacket();
                byte[] packet = intToByteArray.changeIntegerArrayToByteArray(pkt);
                System.out.println(packet);
                System.out.println("Sent one packet with header=" + pkt[0]);
                swProtocol.sendToOtherLayerIfServer(packet);
                //wait a second for acks after sending each packet
                receivedAckSet = checkForAcks(receivedAckSet);

            }
        }
        System.out.println("out of forloop");

    }

    private void sendPacketsIfClient(){
        for(packetCounter = 0; packetCounter <= upperBound ;packetCounter++){
            //packet isn't acknowledged yet and (isn't added to the map or when added false)
            if(!receivedAckSet.contains(packetCounter) && (!ackGivenForPacket.containsKey(packetCounter) || ackGivenForPacket.get(packetCounter))){
                // send/resend packet
                filePointer = DATASIZE * packetCounter;
                Integer[] pkt = createSwPacket();
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                byte[] packet = intToByteArray.changeIntegerArrayToByteArray(pkt);
                swProtocol.getClientHandler();
            }
        }

    }

    private Integer[] createSwPacket(){
        Integer[] pkt = null;
        //send entire datasize or last packet
        int dataSizeToSend = Math.min(DATASIZE, fileContents.length - filePointer);
        if(dataSizeToSend >= 0){
            pkt = new Integer[dataSizeToSend + HEADERSIZE];
            //TODO set pkt1&2 as ack&seq and reset headersize
            pkt[0] = packetCounter;
            System.arraycopy(fileContents,filePointer,pkt,HEADERSIZE,dataSizeToSend);
        }
        return pkt;
    }

    private Set<Integer> checkForAcks(Set<Integer> receivedAcks) {
        System.out.println("check for acks");
        DatagramPacket receivedDatagramPacket = setUpPacketStructure();
        for (int i = 1; i <= NUMBEROFACKCHECKS; i++) {
                try {
                    downloadSocket.receive(receivedDatagramPacket);  //method that blocks until an ACKpacket is received
                } catch (IOException e) {
                    e.printStackTrace();
                }
                int ackNr = extractAck(receivedDatagramPacket);
                if(ackGivenForPacket.size() != 0 ) {
                    receivedAcks.add(ackNr);
                    ackGivenForPacket.put(ackNr, true);
                    System.out.println("received ACK: " + ackNr);
                    return receivedAcks;
                }else if(!ackGivenForPacket.keySet().contains(ackNr)) {
                    receivedAcks.add(ackNr);
                    ackGivenForPacket.put(ackNr,true);
                    System.out.println("received ACK: " +ackNr);
                    return receivedAcks;
                }else{
                    System.out.println("blabloebla");
                }
        }
        return receivedAcks;
    }

    private void createSocket(){
        DatagramSocket downloadSocket = null;
        try {
            downloadSocket = new DatagramSocket(7777);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        this.downloadSocket = downloadSocket;
    }

    private DatagramPacket setUpPacketStructure(){
        //set up the packet structure for the received packets
        int dataLength = 1024;
        byte[] receivedDataBuffer = new byte[dataLength]; //create buffer
        DatagramPacket receivedDatagramPacket = new DatagramPacket(receivedDataBuffer,receivedDataBuffer.length); //create DGpacket
        return receivedDatagramPacket;

    }
    private Integer extractAck(DatagramPacket receivedPacket){
        //extract the received packet data
        InetAddress otherIPAddress = receivedPacket.getAddress();            //the IPaddres from the client
        System.out.println("|UDPServer|  received packet from " + otherIPAddress);

        int serverPort = receivedPacket.getPort();                          //the portnumber used by the client to send this packet
        try {
            swProtocol.getServerHandler().extractedCommand(swProtocol.getServer(),receivedPacket, otherIPAddress,serverPort,downloadSocket);                //extract broadcast message to avoid loop
        } catch (IOException e) {
            e.printStackTrace();
        }
        swProtocol.setDataPart(swProtocol.getServerHandler().getPackageD().getDataPart());
        byte[] recACKData = swProtocol.getServerHandler().getPackageD().getDataPart();

        Integer[] recValue = byteToIntArray.ByteToIntArray(recACKData);
        int packetIndexNumber = recValue[0].intValue();
        System.out.println("PIN " + packetIndexNumber);
        return packetIndexNumber;
    }

}