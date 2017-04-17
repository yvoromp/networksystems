package com.nedap.university.slidingWindowProtocol;

import com.nedap.university.client.UDPClient.commandHandlerOfClient;
import com.nedap.university.server.UDPServer.UDPServer;
import com.nedap.university.server.UDPServer.commandHandlerOfServer;
import com.nedap.university.utils.ByteToIntArray;
import com.nedap.university.utils.IntToByteArray;
import com.nedap.university.utils.SetFileContents;
import com.nedap.university.utils.getFileContents;
import com.nedap.university.client.UDPClient.UDPClient;

import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * Created by yvo.romp on 14/04/2017.
 */
public class SwProtocol {

    private UDPClient client;
    private UDPServer server;
    private commandHandlerOfServer serverHandler;
    private commandHandlerOfClient clientHandler;
    private Map<Integer, Integer[]> packetMap = new HashMap<>();
    private Integer[] fileContent;
    private byte[] dataPart;
    private int fileID;

    private IntToByteArray intToByteArray = new IntToByteArray();
    private ByteToIntArray byteToIntArray = new ByteToIntArray();

    private static final int DATASIZE = 1000;

    /**
     * if server
     * @param server
     * @param dataPart
     */
    public SwProtocol(UDPServer server, commandHandlerOfServer serverHandler, byte[] dataPart){
        this.server = server;
        this.serverHandler = serverHandler;
        this.dataPart = dataPart;
        getFileContents getFileContents = new getFileContents();
        fileID = ByteBuffer.wrap(dataPart).getInt();
        System.out.println("SWFileid:  " + fileID);
        Integer [] fileContents = getFileContents.getFileContentsIfServer(fileID);
        fileContent = fileContents;
    }

    /**
     * if client
     * @param client
     * @param dataPart
     */
    public SwProtocol(UDPClient client,commandHandlerOfClient clientHandler, byte[] dataPart){
        this.client = client;
        this.clientHandler = clientHandler;
        this.dataPart = dataPart;
        getFileContents getFileContents = new getFileContents();
        getFileContents.getFileContentsIfClient(this.fileID);
    }



    //run the program as a sender
    public void runAsSenderIfServer(){
        System.out.println("sending files.....");

        //read from the input file
        int filePointer = 0;
        Set<Integer> receivedAck = new HashSet<>();
        int totalNumberOfPackets = fileContent.length / DATASIZE + 1;
        System.out.println("datasize : " + DATASIZE);
        System.out.println("total packets to send :  " + totalNumberOfPackets);
        //start track and trace of packets
        TrackAndTrace trackAndTrace = new TrackAndTrace();
        trackAndTrace.startTrackTraceAsServer(this, receivedAck,totalNumberOfPackets,filePointer,fileContent);
    }

    //run the program as a sender
    public void runAsSenderIfClient(){
        System.out.println("sending files.....");

        //read from the input file
        int filePointer = 0;
        Set<Integer> receivedAck = new HashSet<>();
        int totalNumberOfPackets = fileContent.length / DATASIZE + 1;
        System.out.println("total packets to send :  " + totalNumberOfPackets);
        System.out.println("contentlength : " + fileContent.length);
        System.out.println("datasize : " + DATASIZE);
        //start track and trace of packets
        TrackAndTrace trackAndTrace = new TrackAndTrace();
        trackAndTrace.startTrackTraceAsClient(this, receivedAck,totalNumberOfPackets,filePointer,fileContent);
    }


    public void runAsReceiverIfClient(){
        System.out.println("receiving files......");
        int highestRecPacket = 0;
        int newPacketPosition = 0;

        boolean keepGoing = true;
        while(keepGoing){
            int packetIndexNumber = Integer.valueOf(dataPart[0]);
            //TODO [1] & [2] (ack & seq)
            System.out.println("Received packet, length = "+dataPart.length+"  first byte = "+packetIndexNumber);

            //append the packet's data part - header to the fileContentsArray
            if(packetIndexNumber > highestRecPacket){
                highestRecPacket = packetIndexNumber;
            }
            if(!packetMap.keySet().contains(packetIndexNumber)){
                Integer[] packetData = byteToIntArray.ByteToIntArray(dataPart);
                packetData = Arrays.copyOfRange(packetData,1,packetData.length);
                packetMap.put(packetIndexNumber,packetData);
            }
            sendAcknowledgementIfClient(packetIndexNumber);

            if(!allPacketsReceived(packetMap,highestRecPacket)&& packetMap.keySet().size() == Collections.max(packetMap.keySet())){
                System.out.println("all packets received!!");
                keepGoing = false;
            }
             //wait 10ms before trying again
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    keepGoing = false;
                }

        }
        buildFile();
    }

    private boolean allPacketsReceived(Map<Integer, Integer[]> packetMap, int highestRecPacket) {
        //check if all packets are received
           boolean allPacketsReceived = false;
            for (int i = 0; i < highestRecPacket; i++) {
                if (packetMap.keySet().contains(i) && packetMap.get(highestRecPacket).length < DATASIZE) {
                    allPacketsReceived = true;
                }
            }
            return allPacketsReceived;

    }

    private void buildFile(){
        int newPacketPosition = 0;
        Integer [] fileContents = new Integer [0];
        for (int index = 1; index <= packetMap.size(); index++) {
            int datalen = packetMap.get(index).length;
            fileContents = Arrays.copyOf(fileContents, newPacketPosition + datalen);
            System.arraycopy(packetMap.get(index), 0, fileContents, newPacketPosition, datalen);
            newPacketPosition = newPacketPosition + packetMap.get(index).length;
        }
        //write as file to outputMap
        concatToFile(fileContents,fileID);
    }

    private void sendAcknowledgementIfClient(int packetIndexNumber) {
        Integer[] ackPacket = createAckPacket(packetIndexNumber);
        byte[] packet = intToByteArray.changeIntegerArrayToByteArray(ackPacket);
        sendToOtherLayerIfClient(packet);
        System.out.println("Send ACK for received packetnumber = " + packetIndexNumber);
    }

    private void concatToFile(Integer[] fileContent, int id){
        SetFileContents.setFileContents(fileContent,id);
    }

    private Integer[] createAckPacket(int packetIndexNumber){
        return new Integer[]{packetIndexNumber};
    }

    public void sendToOtherLayerIfClient(byte[] packet){
        clientHandler.setActiveDownload(true);
        DatagramPacket totalPacket = clientHandler.makeDatagramPacket(clientHandler.getOtherIPAddress(),clientHandler.getClientPort(),packet);
        clientHandler.sendDatagramPacket(totalPacket);
    }

    public void sendToOtherLayerIfServer(byte[] packet){
        serverHandler.setActiveDownload(true);
        DatagramPacket totalPacket = serverHandler.makeDatagramPacket(serverHandler.getOtherIP(),serverHandler.getClientPort(),packet);
        serverHandler.sendDatagramPacket(totalPacket);
    }

    //sets the network/UDP layer implementation for client
    public void setNetworkLayer(UDPClient udpClient){
        client = udpClient;
    }

    //sets the network/UDP layer implementation for server
    public void setNetworkLayer(UDPServer udpServer){
        server = udpServer;
    }

    public UDPClient getClient() {
        return client;
    }

    public UDPServer getServer() {
        return server;
    }

    public int getFileID() {
        return fileID;
    }

    public void setFileID(int fileID) {
        this.fileID = fileID;
    }

    public commandHandlerOfServer getServerHandler() {
        return serverHandler;
    }

    public commandHandlerOfClient getClientHandler() {
        return clientHandler;
    }
}
