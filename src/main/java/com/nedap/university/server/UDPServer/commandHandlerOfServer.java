package com.nedap.university.server.UDPServer;

import com.nedap.university.UDPpackageStructure.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by yvo.romp on 10/04/2017.
 */


public class commandHandlerOfServer {

    private DatagramSocket clientSocket;
    private DatagramSocket downloadSocket;
    private UDPServer server;
    private UDPheader UDPheader;
    private packageCreator packageC;
    private PackageDissector packageD;
    private UDPFlags flags;
    private FlagActions flagActions;
    private InetAddress otherIP;
    private int clientPort;
    private int serverPort = 5555;
    private int downloadPort = 7777;
    private boolean activeDownload = false;

    //deepCopy of packet
    private DatagramPacket deepCopyPacket;

    //deal with the given command by the client
    public void extractedCommand(UDPServer UDPServer, DatagramPacket receivedPacket, InetAddress otherIPAddress, int clientPort, DatagramSocket UDPClientSocket) throws IOException{
        otherIP = otherIPAddress;
        this.clientPort = clientPort;
        cutOfTheHead(receivedPacket);
        flags = new UDPFlags(this);
        server = UDPServer;
        flagActions = new FlagActions(this);
        clientSocket = UDPClientSocket;
        flagActions.takeFlagActions(UDPServer,receivedPacket,otherIPAddress,clientPort,UDPClientSocket);
    }

    //make a new datagram packet to send with string as data
    public DatagramPacket makeDatagramPacket(InetAddress otherIPAddress, int clientPort,String serverAnswer){
        byte[] returnData;
        String returnMessage = serverAnswer;
        returnData = returnMessage.getBytes();

        if(activeDownload){
            UDPheader = new UDPheader(downloadPort,downloadPort,5,flags.checkForFlags(),0);

        }else{
            UDPheader = new UDPheader(serverPort,clientPort,5,flags.checkForFlags(),0);
        }
        packageC = new packageCreator();
        returnData = packageC.packageCreator(UDPheader,returnData);
        DatagramPacket returnPacket = new DatagramPacket(returnData, returnData.length, otherIPAddress, clientPort);
        return returnPacket;
    }

    //make a new datagram packet to send with byteArray as data
    public DatagramPacket makeDatagramPacket(InetAddress otherIPAddress, int clientPort,byte[] serverAnswer){
        DatagramPacket returnPacket;
        if(activeDownload){
            UDPheader = new UDPheader(downloadPort,downloadPort,5,flags.checkForFlags(),0);
            packageC = new packageCreator();
            serverAnswer = packageC.packageCreator(UDPheader,serverAnswer);
            returnPacket = new DatagramPacket(serverAnswer, serverAnswer.length, otherIPAddress, downloadPort);

        }else{
            UDPheader = new UDPheader(serverPort,clientPort,5,flags.checkForFlags(),0);
            packageC = new packageCreator();
            serverAnswer = packageC.packageCreator(UDPheader,serverAnswer);
            returnPacket = new DatagramPacket(serverAnswer, serverAnswer.length, otherIPAddress, clientPort);
        }
        return returnPacket;
    }

    //sends the datagram packet as an answer on the command from the client
    public void sendDatagramPacket(DatagramPacket packetToSend){
        //make a deep copy that can be resend ico no ack-return packet
        makeDeepCopyOfPacket(packetToSend);
        //reset the flags to false
        flags.resetFlags();
        if(activeDownload){
            try {
                downloadSocket.send(packetToSend);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            try {
                clientSocket.send(packetToSend);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    //cuts of the header and leaves the datapart
    public void cutOfTheHead(DatagramPacket packet){
        packageD = new PackageDissector(packet);

    }

    public void makeDeepCopyOfPacket(DatagramPacket packetToSend){
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

    public InetAddress getOtherIP() {
        return otherIP;
    }

    public int getClientPort() {
        return clientPort;
    }

    public int getDownloadPort() {
        return downloadPort;
    }

    public void setDownloadSocket(DatagramSocket downloadSocket) {
        this.downloadSocket = downloadSocket;
    }

    public void setActiveDownload(boolean activeDownload) {
        this.activeDownload = activeDownload;
    }


}
