package com.nedap.university.slidingWindowProtocol;

import com.nedap.university.client.UDPClient.UDPClient;
import com.nedap.university.server.UDPServer.UDPServer;

/**
 * Created by yvo.romp on 14/04/2017.
 */
public abstract class SwProtocol implements TimeOutHandler{

    private UDPClient client;
    private UDPServer server;
    private int FileID;

    //run the program as a sender
    public abstract void runAsSender();


    //run the program as a receiver
    public abstract void runAsReceiver();

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
        return FileID;
    }

    public void setFileID(int fileID) {
        FileID = fileID;
    }
}
