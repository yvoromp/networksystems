package com.nedap.university.slidingWindowProtocol;

import com.nedap.university.utils.ByteToIntArray;
import com.nedap.university.utils.IntToByteArray;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * Created by yvo.romp on 15/04/2017.
 */



public class TrackAndTrace {

    private SwProtocol swProtocol;
    private byte[] fileContents;
    private Set<Integer> receivedAckSet;
    private Map<Integer, Boolean> ackGivenForPacket;
    private IntToByteArray intToByteArray = new IntToByteArray();
    private ByteToIntArray byteToIntArray = new ByteToIntArray();

    private static final int WINDOWSIZE = 250;
    private static final int DATASIZE = 500;
    private static final int HEADERSIZE = 4;
    private static final int NUMBEROFACKCHECKS = 50;

    private int packetCounter;
    private int lowerBound;
    private int upperBound;
    private int filePointer;

    private DatagramSocket downloadSocket;


    public void startTrackTraceAsServer(SwProtocol sWP, Set<Integer> receivedAckSet, int totalNumberOfPackets, int filePointer, byte[] fileContent){
        swProtocol = sWP;
        fileContents = fileContent;
        ackGivenForPacket = new HashMap<>();
        this.receivedAckSet = receivedAckSet;
        createSocket();
        swProtocol.getServerHandler().setDownloadSocket(downloadSocket);
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

    public void startTrackTraceAsClient(SwProtocol sWP, Set<Integer> receivedAckSet, int totalNumberOfPackets, int filePointer, byte[] fileContent){
        swProtocol = sWP;
        fileContents = fileContent;
        ackGivenForPacket = new HashMap<>();
        this.receivedAckSet = receivedAckSet;
        createSocket();
        swProtocol.getClientHandler().setDownloadSocket(downloadSocket);
        while (receivedAckSet.size() != totalNumberOfPackets){
            //sets the lower and upperbound of the senderwindow
            if(receivedAckSet.isEmpty()){
                lowerBound = 1;
                upperBound = Math.min(WINDOWSIZE,totalNumberOfPackets);
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
        for(packetCounter = lowerBound; packetCounter <= upperBound ;packetCounter++){
            System.out.println("packetcounter:  " +packetCounter);
            //packet isn't acknowledged yet and (isn't added to the map or when added false)
            if(!receivedAckSet.contains(packetCounter) && (!ackGivenForPacket.containsKey(packetCounter) || ackGivenForPacket.get(packetCounter))){
                // send/resend packet
                filePointer = DATASIZE * (packetCounter -1) ;
                byte[] pkt = createSwPacket();
                System.out.println("Sent one packet with header=" + packetCounter);
                swProtocol.sendToOtherLayerIfServer(pkt);
                receivedAckSet = checkForAcksIfServer(receivedAckSet);

            }
        }
        System.out.println("total window send");
        sendFinPacketIfServer();

    }

    private void sendFinPacketIfServer(){
        byte[] pkt =intToByteArray.changeIntToByteArray(-upperBound);
        System.out.println("Sent FIN packet with header =" + -upperBound);
        swProtocol.sendToOtherLayerIfServer(pkt);
    }

    private void sendPacketsIfClient(){
        for(packetCounter = lowerBound; packetCounter <= upperBound ;packetCounter++){
            //packet isn't acknowledged yet and (isn't added to the map or when added false)
            if(!receivedAckSet.contains(packetCounter) && (!ackGivenForPacket.containsKey(packetCounter) || ackGivenForPacket.get(packetCounter))){
                // send/resend packet
                filePointer = DATASIZE * packetCounter;
                byte[] pkt = createSwPacket();
                System.out.println("Sent one packet with header=" + packetCounter);
                swProtocol.sendToOtherLayerIfClient(pkt);
                receivedAckSet = checkForAcksIfClient(receivedAckSet);

            }
        }
        System.out.println("total window send");
        sendFinPacketIfClient();
    }

    private void sendFinPacketIfClient(){
        byte[] finPacket = new byte[4];
        finPacket = intToByteArray.changeIntToByteArray(-upperBound);
        System.out.println("Sent FIN packet with header=" + packetCounter);
        swProtocol.sendToOtherLayerIfClient(finPacket);
    }

    private byte[] createSwPacket(){
        byte [] pkt = null;
        //send entire datasize or last packet
        int dataSizeToSend = Math.min(DATASIZE, fileContents.length - filePointer);
        if(dataSizeToSend >= 0){
            pkt = new byte[dataSizeToSend + HEADERSIZE];
            System.out.println("dataSizeToSend  " + dataSizeToSend);
            byte [] pktCounter = intToByteArray.changeIntToByteArray(packetCounter);
            System.out.println("pktC: " + pktCounter.length);
            System.arraycopy(pktCounter,0,pkt,0,pktCounter.length);
            System.arraycopy(fileContents,filePointer,pkt,pktCounter.length,dataSizeToSend);
        }
        return pkt;
    }

    private Set<Integer> checkForAcksIfServer(Set<Integer> receivedAcks) {
        System.out.println("check for acks");
        DatagramPacket receivedDatagramPacket = setUpPacketStructure();
        for (int i = 1; i <= NUMBEROFACKCHECKS; i++) {
                try {
                    downloadSocket.receive(receivedDatagramPacket);  //method that blocks until an ACKpacket is received
                } catch (IOException e) {
                    e.printStackTrace();
                }
                int ackNr = extractAckIfServer(receivedDatagramPacket);
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
                }
        }
        return receivedAcks;
    }

    private Set<Integer> checkForAcksIfClient(Set<Integer> receivedAcks) {
        System.out.println("checking for acks");
        DatagramPacket receivedDatagramPacket = setUpPacketStructure();
        for (int i = 1; i <= NUMBEROFACKCHECKS; i++) {
            try {
                downloadSocket.receive(receivedDatagramPacket);  //method that blocks until an ACKpacket is received
            } catch (IOException e) {
                e.printStackTrace();
            }
            int ackNr = extractAckIfClient(receivedDatagramPacket);
            if(ackGivenForPacket.size() != 0 ) {
                receivedAcks.add(ackNr);
                ackGivenForPacket.put(ackNr, true);
                System.out.println("received ACKnr: " + ackNr);
                return receivedAcks;
            }else if(!ackGivenForPacket.keySet().contains(ackNr)) {
                receivedAcks.add(ackNr);
                ackGivenForPacket.put(ackNr, true);
                System.out.println("received ACKnr: " + ackNr);
                return receivedAcks;
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
    private Integer extractAckIfServer(DatagramPacket receivedPacket){
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
        int packetIndexNumber = chop(recACKData);
        System.out.println("PIN " + packetIndexNumber);
        return packetIndexNumber;
    }

    private Integer extractAckIfClient(DatagramPacket receivedPacket){
        InetAddress otherIPAddress = receivedPacket.getAddress();            //the IPaddres from the client
        System.out.println("|UDPClient|  received packet from " + otherIPAddress);

        int clientPort = receivedPacket.getPort();
        swProtocol.getClientHandler().extractedCommand(swProtocol.getClient(),receivedPacket, otherIPAddress,clientPort,downloadSocket);                //extract broadcast message to avoid loop

        swProtocol.setDataPart(swProtocol.getClientHandler().getPackageD().getDataPart());
        byte[] recACKData = swProtocol.getClientHandler().getPackageD().getDataPart();
        int packetIndexNumber = chop(recACKData);
        System.out.println("PIN " + packetIndexNumber);
        return packetIndexNumber;
    }

    private static int chop(byte[] receivedDatagramData){
        byte[] result = new byte[4];
        System.arraycopy(receivedDatagramData,20, result,0, 4);
        System.out.println("chopp-ed" + Arrays.toString(result));
        ByteToIntArray byteToIntArray = new ByteToIntArray();
        return (int) byteToIntArray.byteArrayToInt(result);

    }

    public DatagramSocket getDownloadSocket() {
        return downloadSocket;
    }
}