package com.nedap.university.client.UDPClient;

import com.nedap.university.FileProtocol.FileProber;
import com.nedap.university.UDPpackageStructure.*;
import com.nedap.university.slidingWindowProtocol.ReceiverThread;
import com.nedap.university.slidingWindowProtocol.SenderThread;
import com.nedap.university.utils.IntToByteArray;
import com.nedap.university.utils.getFileContents;

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
    private DatagramSocket downloadSocket;
    private InetAddress broadcastIPAddress;
    private InetAddress otherIPAddress;
    private int clientPort = 5555;
    private int serverPort;
    private UDPClient client;
    private UDPheader UDPheader;
    private UDPchecksum udPchecksum = new UDPchecksum();
    private packageCreator packageC;
    private PackageDissector packageD;
    private UDPFlags flags;
    private FlagActions flagActions;
    private HashMap lastRequestedFileList;
    private int downloadPort = 7777;
    private boolean activeDownload = false;
    private boolean activeUpload = false;

    private boolean isBroadcast = false;

    //deepCopy of packet
    private DatagramPacket deepCopyPacket;

    //deal with the given command by the client
    public void extractedCommand(UDPClient UDPclient, DatagramPacket receivedPacket, InetAddress otherIPAddress, int serverPort, DatagramSocket UDPServerSocket) {
        this.otherIPAddress = otherIPAddress;
        this.serverPort = serverPort;
        cutOfTheHead(receivedPacket);
        client = UDPclient;
        serverSocket = UDPServerSocket;
        flags = new UDPFlags(this);
        udPchecksum = new UDPchecksum();
        flagActions = new FlagActions(this);
        flagActions.takeFlagActions(UDPclient, receivedPacket, otherIPAddress, serverPort, UDPServerSocket);

    }

    //make a new datagram packet to send with byteArray as data
    public DatagramPacket makeDatagramPacket(InetAddress otherIPAddress, int serverPort,byte[] clientAnswer){
        DatagramPacket returnPacket;
        if(activeDownload){
            UDPheader = new UDPheader(downloadPort, downloadPort, 5, flags.checkForFlags(), udPchecksum.getTotalChecksum(clientAnswer));
            packageC = new packageCreator();
            clientAnswer = packageC.packageCreator(UDPheader,clientAnswer);
            returnPacket = new DatagramPacket(clientAnswer, clientAnswer.length, otherIPAddress, downloadPort);
        }else{
            UDPheader = new UDPheader(clientPort, serverPort, 5, flags.checkForFlags(), udPchecksum.getTotalChecksum(clientAnswer));
            packageC = new packageCreator();
            clientAnswer = packageC.packageCreator(UDPheader,clientAnswer);
            returnPacket = new DatagramPacket(clientAnswer, clientAnswer.length, otherIPAddress, serverPort);
        }
        return returnPacket;
    }

    //sends the datagram packet as an answer on the command from the client
    public void sendDatagramPacket(DatagramPacket packetToSend) {
        //make a deep copy that can be resend ico no ack-return packet
        makeDeepCopyOfPacket(packetToSend);
        //resets the flag status'
        flags.resetFlags();
        if(activeDownload){
            try {
                downloadSocket.send(packetToSend);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            try {
                serverSocket.send(packetToSend);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendBroadcastMessage(InetAddress broadcastIPAddress, int clientPortNumber, MulticastSocket multicastSocket, DatagramPacket packetStructure) {
        serverSocket = multicastSocket;
        byte[] returnData;
        String returnMessage = "broadcast";
        returnData = returnMessage.getBytes();
        flags = new UDPFlags(this);
        flags.setBC();

        UDPheader = new UDPheader(clientPort, clientPortNumber, 5, flags.checkForFlags(), udPchecksum.getTotalChecksum(returnData));
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
        flags = new UDPFlags(this);
        flags.setRequest();

        UDPheader = new UDPheader(clientPort, clientPort, 5, flags.checkForFlags(), udPchecksum.getTotalChecksum(returnData));
        packageC = new packageCreator();
        returnData = packageC.packageCreator(UDPheader, returnData);
        DatagramPacket broadcastPacket = new DatagramPacket(returnData, returnData.length, broadcastIPAddress, clientPort);
        try {
            serverSocket.send(broadcastPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void receiveList(DatagramPacket packet) throws IOException, ClassNotFoundException{
        FileProber clientFiles = new FileProber();
        cutOfTheHead(packet);
        clientFiles.filenamesToReceive(packageD.getDataPart());
        setLastRequestedFileList(clientFiles.getFileMap());
    }

    public void printFiles(){
        FileProber clientFiles = new FileProber();
        clientFiles.printAllFilesOfClient();
    }

    public void sendDownloadMessage(int fileID){
        String fileName = client.getCommandHandlerOfClient().getLastRequestedFileList().get(fileID).toString();
        IntToByteArray intToByte = new IntToByteArray();
        byte[] returnData = intToByte.changeIntToByteArray(fileID);
        flags = new UDPFlags(this);
        flags.setReqAnswer();
        UDPheader = new UDPheader(clientPort, serverPort, 5, flags.checkForFlags(), udPchecksum.getTotalChecksum(returnData));
        packageC = new packageCreator();
        returnData = packageC.packageCreator(UDPheader, returnData);
        DatagramPacket addressedPacket = new DatagramPacket(returnData, returnData.length, otherIPAddress, clientPort);
        try {
            serverSocket.send(addressedPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ReceiverThread receiverThread = new ReceiverThread(client,client.getCommandHandlerOfClient(),packageD,flags,fileName);
        receiverThread.start();
    }

    public void sendUploadMessage(int fileID){
        sendNewPortMessage(fileID);
    }

    public void sendNewPortMessage(int fileID){
        IntToByteArray intToByteArray = new IntToByteArray();
        FileProber clientFiles = new FileProber();
        byte[] iDArr = intToByteArray.changeIntToByteArray(fileID);
        String changedFilename = clientFiles.probeForFilenameClientMap(fileID).replaceAll("[^a-zA-Z0-9_\\-.]","");
        changedFilename = changedFilename.toLowerCase();
        byte[] fileName = changedFilename.getBytes();
        byte[] returnData = new byte[iDArr.length + fileName.length];
        System.arraycopy(iDArr,0,returnData,0,iDArr.length);
        System.arraycopy(fileName,0,returnData,iDArr.length,fileName.length);

        setActiveUpload(true);
        flags = new UDPFlags(this);
        flags.setNewPort();

        UDPheader = new UDPheader(clientPort, downloadPort, 5, flags.checkForFlags(), udPchecksum.getTotalChecksum(returnData));
        packageC = new packageCreator();
        returnData = packageC.packageCreator(UDPheader, returnData);
        DatagramPacket addressedPackage = new DatagramPacket(returnData, returnData.length, otherIPAddress, clientPort);
        try {
            serverSocket.send(addressedPackage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //cuts of the header and leaves the datapart
    public void cutOfTheHead(DatagramPacket packet) {
        packageD = new PackageDissector(packet);
    }

    public void makeDeepCopyOfPacket(DatagramPacket packetToSend) {
        int port = packetToSend.getPort();
        byte[] data = packetToSend.getData();
        deepCopyPacket = new DatagramPacket(data,port);
    }

    public PackageDissector getPackageD() {
        return packageD;
    }

    public UDPFlags getFlags() {
        return flags;
    }

    public InetAddress getOtherIPAddress() {
        return otherIPAddress;
    }

    public int getDownloadPort() {
        return downloadPort;
    }

    public void setDownloadSocket(DatagramSocket downloadSocket) {
        this.downloadSocket = downloadSocket;
    }

    public void setBroadcastIPAddress(InetAddress broadcastIPAddress) {
        this.broadcastIPAddress = broadcastIPAddress;
    }

    public void setActiveDownload(boolean activeDownload) {
        this.activeDownload = activeDownload;
    }

    public boolean isActiveDownload() {
        return activeDownload;
    }

    public boolean isActiveUpload() {
        return activeUpload;
    }

    public void setActiveUpload(boolean activeUpload) {
        this.activeUpload = activeUpload;
    }

    public HashMap getLastRequestedFileList() {
        return lastRequestedFileList;
    }

    public void setLastRequestedFileList(HashMap lastRequestedFileList) {
        this.lastRequestedFileList = lastRequestedFileList;
    }
}



