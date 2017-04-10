package com.nedap.university.client.UDPClient;

/**
 * Created by yvo.romp on 07/04/2017.
 */

import java.io.IOException;
import java.net.*;
import java.util.HashMap;

/**
 * functions when this side operates as a receiver
 */
public class UDPClient extends Thread{

    private commandHandlerOfClient commandHandlerOfClient;
    private HashMap<InetAddress,Integer> connectionsMap;

    public void run () {

        System.out.println("|UDPClient| UDPClient started");

        //creating a name for the serverHost
        String serverHostName = "192.168.40.255";       //rasppinet
        //String serverHostName = "192.168.2.255";        //homenet
        //String serverHostName = "10.30.18.255";


        //creating the server address
        InetAddress broadcastIPAddress = null;

        //creating the portnumber (same number as the UDPServer port)
        int serverPortNumber = 5555;

        //creating clientSocket that will be used to contact UDPServer
        MulticastSocket UDPClientSocket = null;

        try {
            UDPClientSocket = new MulticastSocket(serverPortNumber);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //find the IP address of the server from its name
        try {
            broadcastIPAddress = InetAddress.getByName(serverHostName);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        //init the list where all the connection-objects will be mapped
        connectionsMap = new HashMap<>();

        System.out.println("|UDPClient| broadcasting to " + broadcastIPAddress + " using portnumber " + serverPortNumber);



        /**
         * while connection is established; send a broadcast packet to the UDP server
         */

        String message = "broadcast";                   //message to broadcast

        //create a buffer
        byte[] dataToSend;
        dataToSend = message.getBytes();

        DatagramPacket packetToSend = new DatagramPacket(dataToSend, dataToSend.length, broadcastIPAddress, serverPortNumber);

        try {
            UDPClientSocket.send(packetToSend);
        } catch (IOException e) {
            e.printStackTrace();
        }

        DatagramPacket receivedDatagramPacket;

        //create a commandHandlerOfClient for received packets
        commandHandlerOfClient = new commandHandlerOfClient();


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
            String receivedMessage = new String(receivedDatagramPacket.getData());      //create the message that is send

            //if the entire buffer isn't used, remove the empty bytes
            receivedMessage = receivedMessage.trim();
            System.out.println("|UDPClient|  received message: " + receivedMessage);




            commandHandlerOfClient.extractedCommand(this,receivedMessage,otherIPAdress,serverPort,UDPClientSocket);


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

}
