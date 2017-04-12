package com.nedap.university.client.UDPClient;

import com.nedap.university.packageStructure.PackageDissector;
import com.nedap.university.packageStructure.UDPheader;
import com.nedap.university.packageStructure.packageCreator;
import com.nedap.university.server.UDPServer.UDPServer;

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
    private PackageDissector packageD;
    private int clientPort = 5555;

    //flags
    private boolean isBC = false;               //1 is activated
    private boolean isACK = false;             //10 is activated
    private boolean isNewPort = false;        //100 is activated
    private boolean isRequest = false;         //1000 is activated
    private boolean isReqAnswer = false;         //10000 is activated

    private int flagValue;
    private final int BC_FLAG_VALUE = 1;
    private final int ACK_FLAG_VAlUE = 10;
    private final int NEWPORT_FLAG_VALUE = 100;
    private final int REQUEST_FLAG_VALUE = 1000;
    private final int REQUEST_ANSWER_FLAG_VALUE = 10000;


    //deal with the given command by the client
    protected void extractedCommand(UDPClient UDPclient, DatagramPacket receivedPacket, InetAddress otherIPAddress, int serverPort, DatagramSocket UDPServerSocket){
        client = UDPclient;
        serverSocket = UDPServerSocket;

        cutOfTheHead(receivedPacket);
        takeFlagActions(UDPclient,receivedPacket,otherIPAddress,clientPort,UDPServerSocket);

    }
    //make a new datagram packet to send
    private DatagramPacket makeDatagramPacket(InetAddress otherIPAddress, int serverPort,String clientAnswer){
        byte[] returnData;
        String returnMessage = clientAnswer;
        returnData = returnMessage.getBytes();

        UDPheader = new UDPheader(clientPort,serverPort,5,checkForFlags(),0);
        packageC = new packageCreator();
        returnData = packageC.packageCreator(UDPheader,returnData);
        DatagramPacket returnPacket = new DatagramPacket(returnData, returnData.length, otherIPAddress, serverPort);
        return returnPacket;
    }

    //sends the datagram packet as an answer on the command from the client
    private void sendDatagramPacket(DatagramPacket packetToSend){
        //resets the flag status'
        System.out.println("flagsValue: " + flagValue);
        resetFlags();

        try {
            serverSocket.send(packetToSend);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendBroadcastMessage(InetAddress broadcastIPAddress, int clientPortNumber, MulticastSocket multicastSocket, DatagramPacket packetStructure){
        serverSocket = multicastSocket;

        //sets the BC flag
        setBC();

        sendDatagramPacket(makeDatagramPacket(broadcastIPAddress,clientPortNumber,"broadcast"));

    }

    //cuts of the header and leaves the datapart
    public void cutOfTheHead(DatagramPacket packet) {
        packageD = new PackageDissector(packet);
    }

    public void takeFlagActions(UDPClient UDPClient, DatagramPacket receivedPacket, InetAddress otherIPAddress, int clientPort, DatagramSocket UDPClientSocket){
        if(packageD.isBC){
            DatagramPacket returnPacket = makeDatagramPacket(otherIPAddress,clientPort,"hellotoyou");
            sendDatagramPacket(returnPacket);
        }
    }


    private int checkForFlags(){
        flagValue = 0;
        if(isBC){
            flagValue += BC_FLAG_VALUE;
        }else if(isACK){
            flagValue += ACK_FLAG_VAlUE;
        }else if(isNewPort){
            flagValue += NEWPORT_FLAG_VALUE;
        }else if(isRequest){
            flagValue += REQUEST_FLAG_VALUE;
        }else if(isReqAnswer){
            flagValue += REQUEST_ANSWER_FLAG_VALUE;
        }
        return flagValue;

    }


    public boolean isBC() {
        return isBC;
    }

    public void setBC() {
        isBC = true;
    }

    public boolean isACK() {
        return isACK;
    }

    public void setACK() {
        isACK = true;
    }

    public boolean isNewPort() {
        return isNewPort;
    }

    public void setNewPort() {
        isNewPort = true;
    }

    public boolean isRequest() {
        return isRequest;
    }

    public void setRequest() {
        isRequest = true;
    }

    public boolean isReqAnswer() {
        return isReqAnswer;
    }

    public void setReqAnswer() {
        isReqAnswer = true;
    }

    public void resetFlags(){
        isACK = false;
        isBC = false;
        isNewPort = false;
        isRequest = false;
        isReqAnswer = false;
    }
}
