package com.nedap.university.client.UDPClient;

/**
 * Created by yvo.romp on 07/04/2017.
 */

import java.io.IOException;
import java.net.*;

/**
 * functions when this side operates as a receiver
 */
public class UDPClient extends Thread{

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
        DatagramSocket UDPClientSocket = null;

        try {
            UDPClientSocket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }

        //find the IP address of the server from its name
        try {
            broadcastIPAddress = InetAddress.getByName(serverHostName);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        System.out.println("|UDPClient| broadcasting to " + broadcastIPAddress + " using portnumber " + serverPortNumber);


        /**
         * while connection is established; send a packet to the UDP server
         */

        String message = "broadcast";                   //message to send

        //create a buffer
        byte[] dataToSend;
        dataToSend = message.getBytes();

        DatagramPacket packetToSend = new DatagramPacket(dataToSend, dataToSend.length, broadcastIPAddress, serverPortNumber);

        try {
            UDPClientSocket.send(packetToSend);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //set up the packet structure for the received packets
        int dataLength = 1024;
        byte[] receivedDataBuffer = new byte[dataLength]; //create buffer
        DatagramPacket receivedDatagramPacket = new DatagramPacket(receivedDataBuffer,receivedDataBuffer.length); //create DGpacket

        while(true) {
            //wait till a packet arrives
            System.out.println("|UDPServer|  Waiting for incoming traffic...");
            try {
                UDPClientSocket.receive(receivedDatagramPacket);  //method that blocks until a packet is received
            } catch (IOException e) {
                e.printStackTrace();
            }

            //extract the received packet data if it contains a String format
            InetAddress otherIPAdress = receivedDatagramPacket.getAddress();            //the IPaddres from the client
            System.out.println("|UDPServer|  received packet from " + otherIPAdress);

            int clientPort = receivedDatagramPacket.getPort();                          //the portnumber used by the client to send this packet
            String receivedMessage = new String(receivedDatagramPacket.getData());      //create the message that is send

            //if the entire buffer isn't used, remove the empty bytes
            receivedMessage = receivedMessage.trim();
            System.out.println("|UDPServer|  received message: " + receivedMessage);

            //send a message back to the the client
            byte[] returnData;
            String returnMessage = "thank you for your message";
            returnData = returnMessage.getBytes();

            DatagramPacket returnPacket = new DatagramPacket(returnData, returnData.length, otherIPAdress, clientPort);
            try {
                UDPClientSocket.send(returnPacket);

            } catch (IOException e) {
                e.printStackTrace();
            }


        }


    }

}
