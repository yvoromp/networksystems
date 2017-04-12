package com.nedap.university.server.UDPServer;

import com.nedap.university.client.UDPClient.UDPClient;
import com.nedap.university.packageStructure.UDPheader;
import com.nedap.university.packageStructure.packageCreator;
import com.nedap.university.packageStructure.PackageDissector;

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
    private PackageDissector packageD;
    private int serverPort = 5555;

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
    protected void extractedCommand(UDPServer UDPServer, DatagramPacket receivedPacket, InetAddress otherIPAddress, int clientPort, DatagramSocket UDPClientSocket){
        server = UDPServer;
        clientSocket = UDPClientSocket;

        cutOfTheHead(receivedPacket);
        takeFlagActions(UDPServer,receivedPacket,otherIPAddress,clientPort,UDPClientSocket);

//        if(receivedMessage.equals("broadcast")){
//            DatagramPacket returnPacket = makeDatagramPacket(otherIPAddress,clientPort,"hellotoyou");
//            sendDatagramPacket(returnPacket);
//        }else if(receivedMessage.equals("6666")){
//            sendDatagramPacket(makeDatagramPacket(otherIPAddress,clientPort,"7777"));
//        }

    }

    //make a new datagram packet to send
    private DatagramPacket makeDatagramPacket(InetAddress otherIPAddress, int clientPort,String serverAnswer){
        byte[] returnData;
        String returnMessage = serverAnswer;
        returnData = returnMessage.getBytes();

        UDPheader = new UDPheader(serverPort,clientPort,5,checkForFlags(),0);
        packageC = new packageCreator();
        returnData = packageC.packageCreator(UDPheader,returnData);
        DatagramPacket returnPacket = new DatagramPacket(returnData, returnData.length, otherIPAddress, clientPort);
        return returnPacket;
    }

    //sends the datagram packet as an answer on the command from the client
    private void sendDatagramPacket(DatagramPacket packetToSend){
        resetFlags();
        try {
            clientSocket.send(packetToSend);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //cuts of the header and leaves the datapart
    public void cutOfTheHead(DatagramPacket packet){
        packageD = new PackageDissector(packet);
        //TODO implement the cutoff and return the data
       //TODO trim message!!! (exclude empty bytes)
        //TODO implement another method where the data is stripped (to discover all headerfields)



    }

    public void takeFlagActions(UDPServer UDPServer, DatagramPacket receivedPacket, InetAddress otherIPAddress, int clientPort, DatagramSocket UDPClientSocket){
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
