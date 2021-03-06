package com.nedap.university.client.UDPClient;

/**
 * Created by yvo.romp on 07/04/2017.
 */

import com.nedap.university.FileProtocol.TerminalInputThread;
import com.nedap.university.UDPpackageStructure.packageCreator;

import java.io.IOException;
import java.net.*;
import java.util.HashMap;

/**
 * functions when this side operates as a receiver
 */
public class UDPClient extends Thread{

    private commandHandlerOfClient commandHandlerOfClient;
    private HashMap<InetAddress,Integer> connectionsMap;
    private packageCreator pCreator;


    public void run () {

        System.out.println("|UDPClient| UDPClient started");

        //creating the portnumber (same number as the UDPServer port)
        int clientPortNumber = 5555;

        //creating clientSocket that will be used to contact UDPServer
        MulticastSocket UDPClientSocket = null;

        try {
            UDPClientSocket = new MulticastSocket(clientPortNumber);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //init the list where all the connection-objects will be mapped
        connectionsMap = new HashMap<>();
        //init the packageCreator
        pCreator = new packageCreator();

        /**
         * while connection is established; send a broadcast packet to the UDP server
         */

        DatagramPacket receivedDatagramPacket;

        //create a commandHandlerOfClient for received packets
        commandHandlerOfClient = new commandHandlerOfClient();
        //start bcthread
        BroadcastThread broadcastThread = new BroadcastThread();
        broadcastThread.start();
        //starts kbithread
        TerminalInputThread terminalInputThread = new TerminalInputThread(this);
        terminalInputThread.start();
        commandHandlerOfClient.setBroadcastIPAddress(broadcastThread.getBroadcastIP());


        while(true) {
            //wait till a packet arrives
            System.out.println("|UDPClient|  Waiting for incoming traffic...");
            //reset packetStructure
            receivedDatagramPacket = setUpPacketStructure();

            try {
                UDPClientSocket.receive(receivedDatagramPacket);  //method that blocks until a packet is received
            } catch (IOException e) {
                e.printStackTrace();
            }

            //extract the received packet data
            InetAddress otherIPAdress = receivedDatagramPacket.getAddress();            //the IPaddres from the client
            System.out.println("|UDPClient|  received packet from " + otherIPAdress);

            int serverPort = receivedDatagramPacket.getPort();                          //the portnumber used by the client to send this packet

            if(!connectionsMap.containsKey(otherIPAdress)){
                connection newConnection = new connection(otherIPAdress,serverPort);
                addConnectionToMap(newConnection);
                System.out.println("connection added");
            }
            commandHandlerOfClient.extractedCommand(this,receivedDatagramPacket,otherIPAdress,serverPort,UDPClientSocket);

        }
    }

    public HashMap<InetAddress, Integer> getConnectionsMap() {
        return connectionsMap;
    }

    public void addConnectionToMap(connection newConnection){
        InetAddress serverAddress = newConnection.getAddress();
        int port = newConnection.getPort();
        connectionsMap.put(serverAddress,port);
        System.out.println(connectionsMap.toString());
    }
    private DatagramPacket setUpPacketStructure(){
        //set up the packet structure for the received packets
        int dataLength = 1024;
        byte[] receivedDataBuffer = new byte[dataLength]; //create buffer
        DatagramPacket receivedDatagramPacket = new DatagramPacket(receivedDataBuffer,receivedDataBuffer.length); //create DGpacket
        return receivedDatagramPacket;

    }

    public commandHandlerOfClient getCommandHandlerOfClient() {
        return commandHandlerOfClient;
    }
}
