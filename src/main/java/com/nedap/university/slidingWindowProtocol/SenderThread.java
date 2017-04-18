package com.nedap.university.slidingWindowProtocol;

import com.nedap.university.UDPpackageStructure.PackageDissector;
import com.nedap.university.UDPpackageStructure.UDPFlags;
import com.nedap.university.client.UDPClient.UDPClient;
import com.nedap.university.client.UDPClient.commandHandlerOfClient;
import com.nedap.university.server.UDPServer.UDPServer;
import com.nedap.university.server.UDPServer.commandHandlerOfServer;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;

/**
 * Created by yvo.romp on 17/04/2017.
 */
public class SenderThread extends Thread {

    private int dpn = 7777;
    private UDPServer udpServer;
    private UDPClient udpClient;
    private commandHandlerOfServer commandHandlerOfServer;
    private commandHandlerOfClient commandHandlerOfClient;
    private PackageDissector packageDissector;
    private UDPFlags udpFlags;
    private boolean runAsServer;

    public SenderThread(UDPServer udpServer, commandHandlerOfServer commandHandlerOfServer, PackageDissector packageDissector, UDPFlags flags){
        runAsServer = true;
        this.udpServer = udpServer;
        this.commandHandlerOfServer = commandHandlerOfServer;
        this.packageDissector = packageDissector;
        udpFlags = flags;

    }

    public SenderThread(UDPClient udpClient,commandHandlerOfClient commandHandlerOfClient, PackageDissector packageDissector, UDPFlags flags){
        runAsServer = false;
        this.udpClient = udpClient;
        this.commandHandlerOfClient = commandHandlerOfClient;
        this.packageDissector = packageDissector;
        udpFlags = flags;

    }

    public void run(){
        if(runAsServer){
            runThreadAsServer();
        }else{
            runThreadAsClient();
        }
    }

    public void runThreadAsClient(){


    }

    public void runThreadAsServer(){
        SwProtocol swProtocol = new SwProtocol(udpServer,commandHandlerOfServer,packageDissector.getDataPart());
            System.out.println("|ST| : running");
            swProtocol.setFileID(ByteBuffer.wrap(packageDissector.getDataPart()).getInt());
            swProtocol.runAsSenderIfServer();
    }


    private DatagramPacket setUpPacketStructure(){
        //set up the packet structure for the received packets
        int dataLength = 1024;
        byte[] receivedDataBuffer = new byte[dataLength]; //create buffer
        DatagramPacket receivedDatagramPacket = new DatagramPacket(receivedDataBuffer,receivedDataBuffer.length); //create DGpacket
        return receivedDatagramPacket;
    }
}
