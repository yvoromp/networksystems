package com.nedap.university.server.UDPServer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by yvo.romp on 10/04/2017.
 */


public class commandHandler {

    private DatagramSocket serverSocket;

    //deal with the given command by the client
    protected void extractedCommand(String receivedMessage, InetAddress otherIPAddress, int clientPort, DatagramSocket UDPServerSocket){
        serverSocket = UDPServerSocket;
        if(receivedMessage.equals("broadcast")){
            DatagramPacket returnPacket = makeDatagramPacket(otherIPAddress,clientPort);
            sendDatagramPacket(returnPacket);
        }

    }

    //make a new datagram packet to send
    private DatagramPacket makeDatagramPacket(InetAddress otherIPAddress, int clientPort){
        byte[] returnData;
        String returnMessage = "hello";
        returnData = returnMessage.getBytes();
        DatagramPacket returnPacket = new DatagramPacket(returnData, returnData.length, otherIPAddress, clientPort);
        return returnPacket;
    }

    //sends the datagram packet as an answer on the command from the client
    private void sendDatagramPacket(DatagramPacket packetToSend){
        try {
            serverSocket.send(packetToSend);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
