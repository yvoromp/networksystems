package com.nedap.university.client.UDPClient;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

/**
 * Created by yvo.romp on 13/04/2017.
 */
public class BroadcastThread extends Thread{

    private commandHandlerOfClient commandHandlerOfClient;
    private String serverHostName = "192.168.40.255";
    private int clientPortNumber = 5555;



    public void run(){

        //creating clientSocket that will be used to contact UDPServer
        MulticastSocket UDPClientSocket = null;

        //creating the server address
        InetAddress broadcastIPAddress = null;

        try {
            UDPClientSocket = new MulticastSocket(clientPortNumber);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //find the IP address of the server from its name
        try {
            broadcastIPAddress = InetAddress.getByName(serverHostName);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }


        //create a commandHandlerOfClient for received packets
        commandHandlerOfClient = new commandHandlerOfClient();

        DatagramPacket receivedDatagramPacket;
        receivedDatagramPacket = setUpPacketStructure();

        while(true){
            System.out.println("BCT sends!");
            commandHandlerOfClient.sendBroadcastMessage(broadcastIPAddress,clientPortNumber,UDPClientSocket, receivedDatagramPacket);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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
