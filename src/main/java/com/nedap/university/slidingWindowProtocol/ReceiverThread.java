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
public class ReceiverThread extends Thread {

    private int dpn = 7777;
    private UDPServer udpServer;
    private UDPClient udpClient;
    private com.nedap.university.server.UDPServer.commandHandlerOfServer commandHandlerOfServer;
    private com.nedap.university.client.UDPClient.commandHandlerOfClient commandHandlerOfClient;
    private PackageDissector packageDissector;
    private boolean runAsServer;
    private UDPFlags udpFlags;
    private String fileName;

    public ReceiverThread(UDPServer udpServer, commandHandlerOfServer commandHandlerOfServer, PackageDissector packageDissector, UDPFlags flags,String fileName){
        runAsServer = true;
        this.udpServer = udpServer;
        this.commandHandlerOfServer = commandHandlerOfServer;
        this.packageDissector = packageDissector;
        udpFlags = flags;
        this.fileName = fileName;

    }

    public ReceiverThread(UDPClient udpClient,commandHandlerOfClient commandHandlerOfClient, PackageDissector packageDissector, UDPFlags flags,String fileName){
        runAsServer = false;
        this.udpClient = udpClient;
        this.commandHandlerOfClient = commandHandlerOfClient;
        this.packageDissector = packageDissector;
        udpFlags = flags;
        this.fileName = fileName;
    }

    public void run(){
        if(runAsServer){
            runThreadAsServer();
        }else{
            runThreadAsClient();
        }

    }

    private void runThreadAsClient(){
        SwProtocol swProtocol = new SwProtocol(udpClient,commandHandlerOfClient,packageDissector.getDataPart());
        DatagramSocket downloadSocket = null;
        try {
            downloadSocket = new DatagramSocket(7777);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        commandHandlerOfClient.setDownloadSocket(downloadSocket);
        boolean running = true;
        while (running) {
            //wait till a datapacket arrives on new portnumber new socket
            System.out.println("|RT|  Waiting for incoming traffic...");
            //reset packetStructure
            DatagramPacket receivedDatagramPacket = setUpPacketStructure();

            try {
                downloadSocket.receive(receivedDatagramPacket);  //method that blocks until a packet is received
            } catch (IOException e) {
                e.printStackTrace();
            }
            //extract the received packet data
            InetAddress otherIPAddress = receivedDatagramPacket.getAddress();            //the IPaddres from the client
            System.out.println("|UDPClient|  received packet from " + otherIPAddress);

            int clientPort = receivedDatagramPacket.getPort();                          //the portnumber used by the client to send this packet
            commandHandlerOfClient.extractedCommand(udpClient,receivedDatagramPacket, otherIPAddress,clientPort,downloadSocket);                //extract broadcast message to avoid loop

            swProtocol.setFileID(ByteBuffer.wrap(packageDissector.getDataPart()).getInt());
            swProtocol.setDataPart(commandHandlerOfClient.getPackageD().getDataPart());
            swProtocol.setFileIDName(fileName);
            swProtocol.runAsReceiverIfClient();
            System.out.println("|RT| :working");
        }
    }

    private void runThreadAsServer(){
        //TODO zie..asclient
        SwProtocol swProtocol = new SwProtocol(udpServer,commandHandlerOfServer,packageDissector.getDataPart());
        DatagramSocket downloadSocket = null;
        try {
            downloadSocket = new DatagramSocket(7777);
        } catch (SocketException e) {
            e.printStackTrace();
        }

        while (true) {
            //wait till a datapacket arrives on new portnumber new socket
            System.out.println("|RT|  Waiting for incoming traffic...");
            //reset packetStructure
            DatagramPacket receivedDatagramPacket = setUpPacketStructure();

            try {
                downloadSocket.receive(receivedDatagramPacket);  //method that blocks until a packet is received
            } catch (IOException e) {
                e.printStackTrace();
            }
            swProtocol.setFileID(ByteBuffer.wrap(packageDissector.getDataPart()).getInt());
            swProtocol.runAsReceiverIfServer();
            //extract the received packet data
            InetAddress otherIPAddress = receivedDatagramPacket.getAddress();            //the IPaddres from the client
            System.out.println("|RT|  received packet from " + otherIPAddress);
            System.out.println("|RT| :working");
        }
    }


    private DatagramPacket setUpPacketStructure(){
        //set up the packet structure for the received packets
        int dataLength = 1024;
        byte[] receivedDataBuffer = new byte[dataLength]; //create buffer
        DatagramPacket receivedDatagramPacket = new DatagramPacket(receivedDataBuffer,receivedDataBuffer.length); //create DGpacket
        return receivedDatagramPacket;

    }


}
