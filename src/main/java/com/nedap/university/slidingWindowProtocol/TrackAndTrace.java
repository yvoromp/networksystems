package com.nedap.university.slidingWindowProtocol;

import com.nedap.university.utils.IntToByteArray;

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

    private static final int WINDOWSIZE = 250;
    private static final int DATASIZE = 500;
    private static final int HEADERSIZE = 1;

    private int packetCounter;
    private int lowerBound;
    private int upperBound;
    private int filePointer;


    public void startTrackTraceAsServer(SwProtocol sWP, Set<Integer> receivedAckSet, int totalNumberOfPackets, int filePointer, Integer[] fileContent){
        swProtocol = sWP;
        fileContents = fileContent;
        ackGivenForPacket = new HashMap<>();
        this.receivedAckSet = receivedAckSet;
        while (receivedAckSet.size() != totalNumberOfPackets){
            //sets the lower and upperbound of the senderwindow
            if(receivedAckSet.isEmpty()){
                lowerBound = 0;
                upperBound = WINDOWSIZE;
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
                lowerBound = 0;
                upperBound = WINDOWSIZE;
            }else{
                lowerBound = setLowerBound(receivedAckSet);
                upperBound = setUpperBound(receivedAckSet,totalNumberOfPackets);
            }
            sendPacketsIfClient();
        }
    }

    private int setLowerBound(Set<Integer> receivedAck){
        for(int i = 0; i < receivedAck.size(); i++){
            if(!receivedAck.contains(i)){
                return i;
            }
        }
        return receivedAck.size();

    }

    private int setUpperBound(Set<Integer> receivedAck, int numberOfPackets){
        int upperBound = setLowerBound(receivedAck)+WINDOWSIZE;
        if(upperBound > numberOfPackets){
            upperBound = numberOfPackets;
        }
        return upperBound;
    }

    private void sendPacketsIfServer(){
        for(packetCounter = 0; packetCounter < upperBound ;packetCounter++){
            //packet isn't acknowledged yet and (isn't added to the map or when added false)
            if(!receivedAckSet.contains(packetCounter) && (!ackGivenForPacket.containsKey(packetCounter) || ackGivenForPacket.get(packetCounter))){
                // send/resend packet
                filePointer = DATASIZE * packetCounter;
                Integer[] pkt = createSwPacket();
                //TODO set timer to wait for the given ack from client
                byte[] packet = intToByteArray.changeIntegerArrayToByteArray(pkt);
                swProtocol.sendToOtherLayer(packet);
            }
        }

    }

    private void sendPacketsIfClient(){
        for(packetCounter = 0; packetCounter <= upperBound ;packetCounter++){
            //packet isn't acknowledged yet and (isn't added to the map or when added false)
            if(!receivedAckSet.contains(packetCounter) && (!ackGivenForPacket.containsKey(packetCounter) || ackGivenForPacket.get(packetCounter))){
                // send/resend packet
                filePointer = DATASIZE * packetCounter;
                Integer[] pkt = createSwPacket();
                //TODO this packet has to be send by UDPprotocol as datapart
                //TODO set timer to wait for the given ack from sender
                byte[] packet = intToByteArray.changeIntegerArrayToByteArray(pkt);
                swProtocol.getClientHandler();
            }
        }

    }

    private Integer[] createSwPacket(){
        Integer[] pkt = null;
        //send entire datasize or last packet
        int dataSizeToSend = Math.min(DATASIZE, fileContents.length - filePointer);
        if(dataSizeToSend > 0){
            pkt = new Integer[dataSizeToSend + HEADERSIZE];
            //TODO set pkt1&2 as ack&seq and reset headersize
            pkt[0] = packetCounter;
            System.arraycopy(fileContents,filePointer,pkt,HEADERSIZE,dataSizeToSend);
        }
        return pkt;

    }

}
