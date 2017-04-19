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
    private Map<Integer, byte[]> packetMap = new HashMap<>();
    private byte[] fileContent;
    private byte[] dataPart;
    private int fileID;
    private String fileIDName;

    private IntToByteArray intToByteArray = new IntToByteArray();
    private ByteToIntArray byteToIntArray = new ByteToIntArray();

    private int highestRecPacket = 0;
    private int newPacketPosition = 0;

    private static final int DATASIZE = 1000;
    private boolean running = true;

    /**
     * if server
     * @param server
     * @param dataPart
     */
    public SwProtocol(UDPServer server, commandHandlerOfServer serverHandler, byte[] dataPart){
        this.server = server;
        this.serverHandler = serverHandler;
        this.dataPart = dataPart;
        fileID = chop(dataPart);
        System.out.println("SWFile ID:  " + fileID);
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
        fileID = chop(dataPart);
        System.out.println("SWFile ID:  " + fileID);
    }

    //run the program as a sender
    public void runAsSenderIfServer(){
        System.out.println("sending files.....");
        //read from the input file
        int filePointer = 0;
        Set<Integer> receivedAck = new HashSet<>();
        getFileContents getFileContents = new getFileContents();
        fileContent = getFileContents.getFileToBytesifServer(this.fileID);
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
        getFileContents getFileContents = new getFileContents();
        fileContent = getFileContents.getFileToBytesifServer(this.fileID);
        int totalNumberOfPackets = fileContent.length / DATASIZE + 1;
        System.out.println("total packets to send :  " + totalNumberOfPackets);
        System.out.println("datasize : " + DATASIZE);
        //start track and trace of packets
        TrackAndTrace trackAndTrace = new TrackAndTrace();
        trackAndTrace.startTrackTraceAsClient(this, receivedAck,totalNumberOfPackets,filePointer,fileContent);
    }


    public void runAsReceiverIfClient(){
        System.out.println("receiving files......");
        int highestRecPacket = 0;

            int packetIndexNumber = (chop(dataPart));
            System.out.println("Received packet, length = "+dataPart.length+"  first byte = "+packetIndexNumber);

            //check for fin
            if(packetMap.keySet().contains(-packetIndexNumber) && packetMap.keySet().size() == -packetIndexNumber){
                System.out.println("all packets received have been received!!");
                buildFileIfClient();
                running = false;
                return;
            }

            //append the packet's data part - header to the fileContentsArray
            if(packetIndexNumber > highestRecPacket){
                highestRecPacket = packetIndexNumber;
            }
            if(!packetMap.keySet().contains(packetIndexNumber)){
                byte[] filePart = new byte[dataPart.length-4];
                System.arraycopy(dataPart,4,filePart,0,filePart.length);
                packetMap.put(packetIndexNumber,filePart);
            }
            sendAcknowledgementIfClient(packetIndexNumber);

            if(!allPacketsReceived(packetMap,highestRecPacket)&& packetMap.keySet().size() == Collections.max(packetMap.keySet())){
                System.out.println("all packets received!!");
                buildFileIfClient();
            }

    }

    public void runAsReceiverIfServer(){
        System.out.println("receiving files......");
        int highestRecPacket = 0;
            int packetIndexNumber = (chop(dataPart));
            System.out.println("Received packet, length = "+dataPart.length+"  first byte = "+packetIndexNumber);

            //check for fin
            if(packetMap.keySet().contains(-packetIndexNumber) && packetMap.keySet().size() == -packetIndexNumber){
                System.out.println("all the packets have been received!!");
                buildFileIfServer();
                running = false;
                return;
            }

            //append the packet's data part - header to the fileContentsArray
            if(packetIndexNumber > highestRecPacket){
                highestRecPacket = packetIndexNumber;
            }
            if(!packetMap.keySet().contains(packetIndexNumber)){
                byte[] filePart = new byte[0];
                System.arraycopy(dataPart,4,filePart,0,dataPart.length-4);
                packetMap.put(packetIndexNumber,filePart);
            }
            sendAcknowledgementIfServer(packetIndexNumber);

            if(!allPacketsReceived(packetMap,highestRecPacket)&& packetMap.keySet().size() == Collections.max(packetMap.keySet())){
                System.out.println("all packets received!!");
                buildFileIfServer();
            }



    }

    private boolean allPacketsReceived(Map<Integer, byte[]> packetMap, int highestRecPacket) {
        //check if all packets are received
           boolean allPacketsReceived = true;
            for (int i = 0; i < highestRecPacket; i++) {
                if (!packetMap.keySet().contains(i) && packetMap.get(highestRecPacket).length < DATASIZE) {
                    allPacketsReceived = false;
                }
            }
            return allPacketsReceived;

    }

    private void buildFileIfClient(){
        int newPacketPosition = 0;
        byte[] fileContents = new byte[0];
        for (int index = 1; index <= packetMap.size(); index++) {
            int datalen = packetMap.get(index).length;
            fileContents = Arrays.copyOf(fileContents, newPacketPosition + datalen);
            System.arraycopy(packetMap.get(index), 0, fileContents,newPacketPosition, datalen);
            newPacketPosition = newPacketPosition + packetMap.get(index).length;
        }
        //write as file to outputMap
        concatToFileIfClient(fileContents,fileIDName);
    }

    private void buildFileIfServer(){
        int newPacketPosition = 0;
        byte[] fileContents = new byte[0];
        for (int index = 1; index <= packetMap.size(); index++) {
            int datalen = packetMap.get(index).length;
            fileContents = Arrays.copyOf(fileContents, newPacketPosition + datalen);
            System.arraycopy(packetMap.get(index), 0, fileContents, newPacketPosition, datalen);
            newPacketPosition = newPacketPosition + packetMap.get(index).length;
        }
        //write as file to outputMap
        concatToFileIfServer(fileContents,fileIDName);
    }

    private void sendAcknowledgementIfClient(int packetIndexNumber) {
        byte[] packet = intToByteArray.changeIntToByteArray(packetIndexNumber);
        sendToOtherLayerIfClient(packet);
        System.out.println("Send ACK for received packetnumber = " + packetIndexNumber);
    }

    private void sendAcknowledgementIfServer(int packetIndexNumber) {
        byte[] packet = intToByteArray.changeIntToByteArray(packetIndexNumber);
        sendToOtherLayerIfServer(packet);
        System.out.println("Send ACK for received packetnumber = " + packetIndexNumber);
    }

    private void concatToFileIfClient(byte[] fileContent, String id){

        SetFileContents setFileContents = new SetFileContents();
        setFileContents.setByteArrToFileClient(fileContent,id);

    }

    private void concatToFileIfServer(byte[] fileContent, String id){

        SetFileContents setFileContents = new SetFileContents();
        setFileContents.setByteArrToFileServer(fileContent,id);

    }

//    private Integer[] createAckPacket(int packetIndexNumber){
//        return new Integer[]{packetIndexNumber};
//    }

    public void sendToOtherLayerIfClient(byte[] packet){
        clientHandler.setActiveDownload(true);
        DatagramPacket totalPacket = clientHandler.makeDatagramPacket(clientHandler.getOtherIPAddress(),clientHandler.getDownloadPort(),packet);
        clientHandler.sendDatagramPacket(totalPacket);
    }

    public void sendToOtherLayerIfServer(byte[] packet){
        serverHandler.setActiveDownload(true);
        DatagramPacket totalPacket = serverHandler.makeDatagramPacket(serverHandler.getOtherIP(),serverHandler.getDownloadPort(),packet);
        serverHandler.sendDatagramPacket(totalPacket);
    }

    public UDPClient getClient() {
        return client;
    }

    public UDPServer getServer() {
        return server;
    }

    public void setFileID(int fileID) {
        this.fileID = fileID;
    }

    private static int chop(byte[] receivedDatagramData){
        byte[] result = new byte[4];
        System.arraycopy(receivedDatagramData,20, result,0, 4);
        System.out.println("chopped" + Arrays.toString(result));
        ByteToIntArray byteToIntArray = new ByteToIntArray();
        return (int) byteToIntArray.byteArrayToInt(result);

    }

    public int getFileID() {
        return fileID;
    }

    public commandHandlerOfServer getServerHandler() {
        return serverHandler;
    }

    public commandHandlerOfClient getClientHandler() {
        return clientHandler;
    }

    public void setFileIDName(String fileIDName) {
        this.fileIDName = fileIDName;
    }

    public void setDataPart(byte[] dataPart) {
        this.dataPart = dataPart;
    }

    public boolean isRunning() {
        return running;
    }
}
