package com.nedap.university.server.UDPServer;

import com.nedap.university.client.UDPClient.UDPClient;
import com.nedap.university.packageStructure.UDPheader;
import com.nedap.university.packageStructure.packageCreator;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by yvo.romp on 10/04/2017.
 */


public class commandHandlerOfServer {

    private DatagramSocket clientSocket;
    private UDPServer server;
    private com.nedap.university.packageStructure.UDPheader UDPheader;
    private packageCreator packageC;
    private int serverPort = 5555;

    //deal with the given command by the client
    protected void extractedCommand(UDPServer UDPServer, String receivedMessage, InetAddress otherIPAddress, int clientPort, DatagramSocket UDPClientSocket){
        server = UDPServer;
        clientSocket = UDPClientSocket;
        cutOfTheHead(receivedMessage);

        if(receivedMessage.equals("broadcast")){
            DatagramPacket returnPacket = makeDatagramPacket(otherIPAddress,clientPort,"hellotoyou");
            sendDatagramPacket(returnPacket);
        }else if(receivedMessage.equals("6666")){
            sendDatagramPacket(makeDatagramPacket(otherIPAddress,clientPort,"7777"));
        }

    }

    //make a new datagram packet to send
    private DatagramPacket makeDatagramPacket(InetAddress otherIPAddress, int clientPort,String serverAnswer){
        byte[] returnData;
        String returnMessage = serverAnswer;
        returnData = returnMessage.getBytes();
        DatagramPacket returnPacket = new DatagramPacket(returnData, returnData.length, otherIPAddress, clientPort);
        return returnPacket;
    }

    //sends the datagram packet as an answer on the command from the client
    private void sendDatagramPacket(DatagramPacket packetToSend){
        try {
            clientSocket.send(packetToSend);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //cuts of the header and leaves the datapart
    //TODO implement the cutoff and return the data
    //TODO implement another method where the data is stripped (to discover all headerfields)
}
