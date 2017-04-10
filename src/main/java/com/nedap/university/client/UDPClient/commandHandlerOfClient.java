package com.nedap.university.client.UDPClient;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.sql.Connection;
import java.util.HashMap;

/**
 * Created by yvo.romp on 10/04/2017.
 */
public class commandHandlerOfClient {

    private DatagramSocket serverSocket;
    UDPClient client;

    //deal with the given command by the client
    protected void extractedCommand(UDPClient UDPclient, String receivedMessage, InetAddress otherIPAddress, int serverPort, DatagramSocket UDPServerSocket){
        client = UDPclient;
        serverSocket = UDPServerSocket;
        if(receivedMessage.equals("hello")){
            //make new connection object and add it to the connections map object of the client
            HashMap<InetAddress,Integer> connectionsMap = client.getConnectionsMap();


            if(!connectionsMap.containsKey(otherIPAddress)){
                connection newConnection = new connection(otherIPAddress,serverPort);
                client.addConnectionToMap(newConnection);
                System.out.println("connection added");
            }
            sendDatagramPacket(makeDatagramPacket(otherIPAddress,serverPort,"6666"));

        }

    }
    //make a new datagram packet to send
    private DatagramPacket makeDatagramPacket(InetAddress otherIPAddress, int serverPort,String clientAnswer){
        byte[] returnData;
        String returnMessage = clientAnswer;
        returnData = returnMessage.getBytes();
        DatagramPacket returnPacket = new DatagramPacket(returnData, returnData.length, otherIPAddress, serverPort);
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
