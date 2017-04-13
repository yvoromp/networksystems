package com.nedap.university.client.UDPClient;

import com.nedap.university.UDPpackageStructure.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * Created by yvo.romp on 10/04/2017.
 */
public class commandHandlerOfClient {

    private DatagramSocket serverSocket;
    private InetAddress broadcastIPAddress;
    private int clientPort = 5555;
    private UDPClient client;
    private UDPheader UDPheader;
    private packageCreator packageC;
    private PackageDissector packageD;
    private UDPFlags flags;
    private FlagActions flagActions;

    private boolean isBroadcast = false;

    //deepCopy of packet
    private DatagramPacket deepCopyPacket;

    //deal with the given command by the client
    protected void extractedCommand(UDPClient UDPclient, DatagramPacket receivedPacket, InetAddress otherIPAddress, int serverPort, DatagramSocket UDPServerSocket) {
        cutOfTheHead(receivedPacket);
        client = UDPclient;
        serverSocket = UDPServerSocket;
        flags = new UDPFlags(this);
        flagActions = new FlagActions(this);
        flagActions.takeFlagActions(UDPclient, receivedPacket, otherIPAddress, clientPort, UDPServerSocket);

    }

    //make a new datagram packet to send
    public DatagramPacket makeDatagramPacket(InetAddress otherIPAddress, int serverPort, String clientAnswer) {
        byte[] returnData;
        String returnMessage = clientAnswer;
        returnData = returnMessage.getBytes();


        UDPheader = new UDPheader(clientPort, serverPort, 5, flags.checkForFlags(), 0);
        packageC = new packageCreator();
        returnData = packageC.packageCreator(UDPheader, returnData);
        DatagramPacket returnPacket = new DatagramPacket(returnData, returnData.length, otherIPAddress, serverPort);
        return returnPacket;
    }

    //sends the datagram packet as an answer on the command from the client
    public void sendDatagramPacket(DatagramPacket packetToSend) {
        //make a deep copy that can be resend ico no ack-return packet
        makeDeepCopyOfPacket(packetToSend);
        //resets the flag status'
        flags.resetFlags();

        try {
            serverSocket.send(packetToSend);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendBroadcastMessage(InetAddress broadcastIPAddress, int clientPortNumber, MulticastSocket multicastSocket, DatagramPacket packetStructure) {
        serverSocket = multicastSocket;
        byte[] returnData;
        String returnMessage = "broadcast";
        returnData = returnMessage.getBytes();

        UDPheader = new UDPheader(clientPort, clientPortNumber, 5, 1, 0);
        packageC = new packageCreator();
        returnData = packageC.packageCreator(UDPheader, returnData);
        DatagramPacket broadcastPacket = new DatagramPacket(returnData, returnData.length, broadcastIPAddress, clientPort);
        try {
            serverSocket.send(broadcastPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendLsMessage(){
        byte[] returnData;
        String returnMessage = "lsMessage";
        returnData = returnMessage.getBytes();

        UDPheader = new UDPheader(clientPort, clientPort, 5, 1001, 0);
        packageC = new packageCreator();
        returnData = packageC.packageCreator(UDPheader, returnData);
        DatagramPacket broadcastPacket = new DatagramPacket(returnData, returnData.length, broadcastIPAddress, clientPort);
        try {
            serverSocket.send(broadcastPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    //cuts of the header and leaves the datapart
    public void cutOfTheHead(DatagramPacket packet) {
        packageD = new PackageDissector(packet);
    }
    public void makeDeepCopyOfPacket(DatagramPacket packetToSend) {
        deepCopyPacket = packetToSend;
    }

    public DatagramPacket getDeepCopyPacket() {
        return deepCopyPacket;
    }

    public packageCreator getPackageC() {
        return packageC;
    }

    public PackageDissector getPackageD() {
        return packageD;
    }

    public UDPFlags getFlags() {
        return flags;
    }

    public boolean isBroadcast() {
        return isBroadcast;
    }

    public InetAddress getBroadcastIPAddress() {
        return broadcastIPAddress;
    }

    public void setBroadcastIPAddress(InetAddress broadcastIPAddress) {
        this.broadcastIPAddress = broadcastIPAddress;
    }
}



