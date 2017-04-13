package com.nedap.university.server.UDPServer;

/**
 * Created by yvo.romp on 07/04/2017.
 */

import com.nedap.university.UDPpackageStructure.packageCreator;

import java.io.IOException;
import java.net.*;

/**
 * class to create UDPserver and a datagram packet to send a packet
 */
public class UDPServer extends Thread{

    private MulticastSocket UDPServerSocket = null;
    private commandHandlerOfServer commandHandlerOfServer;
    private packageCreator pCreator;

    public void run() {

        System.out.println("|UDPServer| UDPServer started");

        //declare the UDP server socket

        int UDPport;

        UDPport = 5555; //  TCP/UDP portnumber

        //creating the UDP server socket with the given port number
        try {
            UDPServerSocket = new MulticastSocket(UDPport);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("|UDPServer|  Datagram Socket activated on port " + UDPport);

        //set up the packet structure for the received packets
        //init the packageCreator
        pCreator = new packageCreator();
        DatagramPacket receivedDatagramPacket;

        //create a commandHandlerOfServer for received packets
        commandHandlerOfServer = new commandHandlerOfServer();

        while (true) {
            //wait till a packet arrives
            System.out.println("|UDPServer|  Waiting for incoming traffic...");
            //reset packetStructure
            receivedDatagramPacket = setUpPacketStructure();

            try {
                UDPServerSocket.receive(receivedDatagramPacket);  //method that blocks until a packet is received
            } catch (IOException e) {
                e.printStackTrace();
            }

            //extract the received packet data
            InetAddress otherIPAddress = receivedDatagramPacket.getAddress();            //the IPaddres from the client
            System.out.println("|UDPServer|  received packet from " + otherIPAddress);

            int clientPort = receivedDatagramPacket.getPort();                          //the portnumber used by the client to send this packet

            commandHandlerOfServer.extractedCommand(this,receivedDatagramPacket, otherIPAddress,clientPort,UDPServerSocket);                //extract broadcast message to avoid loop

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
