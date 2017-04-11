package com.nedap.university.client.UDPClient;

import com.nedap.university.packageStructure.UDPheader;
import com.nedap.university.packageStructure.packageCreator;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.HashMap;

/**
 * Created by yvo.romp on 10/04/2017.
 */
public class commandHandlerOfClient {

    private DatagramSocket serverSocket;
    private UDPClient client;
    private UDPheader UDPheader;
    private packageCreator packageC;
    private int clientPort = 5555;

    //deal with the given command by the client
    protected void extractedCommand(UDPClient UDPclient, String receivedMessage, InetAddress otherIPAddress, int serverPort, DatagramSocket UDPServerSocket){
        client = UDPclient;
        serverSocket = UDPServerSocket;
        if(receivedMessage.equals("hellotoyou")){
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
        UDPheader = new UDPheader(clientPort,serverPort,5,0,0);
        packageC = new packageCreator();
        returnData = packageC.packageCreator(UDPheader,returnData);
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

    public void sendBroadcastMessage(InetAddress broadcastIPAddress, int clientPortNumber, MulticastSocket UDPClientSocket){
        String message = "broadcast";                   //message to broadcast

        //create a buffer
        byte[] dataToSend;
        dataToSend = message.getBytes();
        DatagramPacket packetToSend = new DatagramPacket(dataToSend, dataToSend.length, broadcastIPAddress, clientPortNumber);

        try {
            UDPClientSocket.send(packetToSend);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
