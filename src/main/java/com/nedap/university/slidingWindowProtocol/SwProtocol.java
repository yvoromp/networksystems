package com.nedap.university.slidingWindowProtocol;

import com.nedap.university.UDPpackageStructure.PackageDissector;
import com.nedap.university.client.UDPClient.UDPClient;
import com.nedap.university.client.UDPClient.commandHandlerOfClient;
import com.nedap.university.server.UDPServer.UDPServer;
import com.nedap.university.server.UDPServer.commandHandlerOfServer;
import com.nedap.university.utils.IntToByteArray;
import com.nedap.university.utils.getFileContents;
import com.sun.corba.se.impl.activation.CommandHandler;

import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by yvo.romp on 14/04/2017.
 */
public class SwProtocol {

    private UDPClient client;
    private UDPServer server;
    private commandHandlerOfServer serverHandler;
    private commandHandlerOfClient clientHandler;
    private Integer[] fileContent;
    private int fileID;

    private static final int DATASIZE = 1000;

    /**
     * if server
     * @param server
     * @param fileID
     */
    public SwProtocol(UDPServer server, commandHandlerOfServer serverHandler, byte[] fileID){
        this.server = server;
        this.serverHandler = serverHandler;
        this.fileID = ByteBuffer.wrap(fileID).getInt();
        getFileContents getFileContents = new getFileContents();
        Integer [] fileContents = getFileContents.getFileContentsIfServer(this.fileID);
        fileContent = fileContents;
    }

    /**
     * if client
     * @param client
     * @param fileID
     */
    public SwProtocol(UDPClient client,commandHandlerOfClient clientHandler, byte[] fileID){
        this.client = client;
        this.clientHandler = clientHandler;
        this.fileID = ByteBuffer.wrap(fileID).getInt();
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
        System.out.println("contentlength : " + fileContent.length);
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
        //start track and trace of packets
        TrackAndTrace trackAndTrace = new TrackAndTrace();
        trackAndTrace.startTrackTraceAsClient(this, receivedAck,totalNumberOfPackets,filePointer,fileContent);
    }


    public void sendToOtherLayer(byte[] packet){
        DatagramPacket totalPacket = serverHandler.makeDatagramPacket(serverHandler.getOtherIP(),serverHandler.getClientPort(),packet);
        serverHandler.sendDatagramPacket(totalPacket);
    }


    //run the program as a receiver
    public void runAsReceiver(){
        
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
