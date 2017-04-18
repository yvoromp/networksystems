package com.nedap.university.UDPpackageStructure;

import com.nedap.university.FileProtocol.FileProber;
import com.nedap.university.client.UDPClient.UDPClient;
import com.nedap.university.client.UDPClient.commandHandlerOfClient;
import com.nedap.university.client.UDPClient.connection;
import com.nedap.university.server.UDPServer.UDPServer;
import com.nedap.university.server.UDPServer.commandHandlerOfServer;
import com.nedap.university.slidingWindowProtocol.ReceiverThread;
import com.nedap.university.slidingWindowProtocol.SenderThread;
import com.nedap.university.slidingWindowProtocol.SwProtocol;
import com.nedap.university.utils.ByteToIntArray;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.sql.Connection;

/**
 * Created by yvo.romp on 13/04/2017.
 */
public class FlagActions {

    private commandHandlerOfServer serverHandler;
    private commandHandlerOfClient clientHandler;
    private UDPFlags flags;
    private PackageDissector packageDissector;

    /**
     * serverpart
     * @param commandHandlerOfServer
     */
    public FlagActions(commandHandlerOfServer commandHandlerOfServer){
        serverHandler = commandHandlerOfServer;
        flags = serverHandler.getFlags();
        //get info about the values in the received package
        packageDissector = serverHandler.getPackageD();
    }

    public void takeFlagActions(UDPServer UDPServer, DatagramPacket receivedPacket, InetAddress otherIPAddress, int clientPort, DatagramSocket UDPClientSocket) throws IOException{

        if(packageDissector.isBC && !packageDissector.isACK){
            flags.setBC();
            flags.setACK();
            DatagramPacket returnPacket = serverHandler.makeDatagramPacket(otherIPAddress,clientPort,"hellotoyou");
            serverHandler.sendDatagramPacket(returnPacket);
        }
        if(packageDissector.isRequest){
            flags.setRequest();
            flags.setACK();
            FileProber sProber = new FileProber();
            byte[] listToSend = sProber.filenamesToSendIfServer();
            DatagramPacket returnPacket = serverHandler.makeDatagramPacket(otherIPAddress,clientPort,listToSend);
            serverHandler.sendDatagramPacket(returnPacket);
        }
        if(packageDissector.isReqAnswer && !packageDissector.isACK){
            flags.setReqAnswer();
            flags.setACK();
            serverHandler.setActiveDownload(true);

            if(isDownloadFromClient()){
                SenderThread senderThread = new SenderThread(UDPServer,serverHandler,packageDissector,flags);
                senderThread.start();
            }else{
                //ReceiverThread receiverThread = new ReceiverThread(UDPServer,serverHandler,packageDissector,flags);
                //receiverThread.start();
            }

        }
        if(packageDissector.isReqAnswer && packageDissector.isACK){
            System.out.println("ack received on applicationlevel");
        }
    }

    /**
     * clientpart
     * @param commandHandlerOfClient
     */
    public FlagActions(commandHandlerOfClient commandHandlerOfClient){
        clientHandler = commandHandlerOfClient;
        flags = clientHandler.getFlags();
        packageDissector = clientHandler.getPackageD();
    }

    public void takeFlagActions(UDPClient UDPClient, DatagramPacket receivedPacket, InetAddress otherIPAddress, int clientPort, DatagramSocket UDPClientSocket){
        //if BC is of own origin
            if (packageDissector.isBC && !packageDissector.isACK) {
                System.out.println("broadcast has been send to subnet!");
            }
            if (packageDissector.isBC && packageDissector.isACK) {
                System.out.println("BC reply received!");
            }
            if (packageDissector.isRequest && !packageDissector.isACK){
                System.out.println("ls request has been send to subnet");
            }
            if (packageDissector.isRequest && packageDissector.isACK){
                System.out.println("request reply for list received!");
                try {
                    clientHandler.receiveList(receivedPacket);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

            }
            if(packageDissector.isReqAnswer && packageDissector.isACK) {
                flags.setReqAnswer();
                flags.setACK();
                //TODO set ico upload the activedown on true (check same object!!)
                if (clientHandler.isActiveUpload()) {
                    //TODO mirror behavior
                    System.out.println("flagactions - mirror");
                    SwProtocol swProtocol = new SwProtocol(UDPClient, clientHandler, packageDissector.getDataPart());
                    swProtocol.runAsReceiverIfClient();
                } else {
//                    clientHandler.setActiveDownload(true);
//                    SwProtocol swProtocol = new SwProtocol(UDPClient, clientHandler, packageDissector.getDataPart());
//                    swProtocol.runAsReceiverIfClient();
                }
            }
//            else {
//                clientHandler.sendDatagramPacket(clientHandler.getDeepCopyPacket());
//                System.out.println("resending package!");
//            }
    }

    private boolean isDownloadFromClient(){
        //check for -fileid or +fileid  (upload or download)
        boolean isDownload;
        byte[] byteArr = packageDissector.getDataPart();
        ByteToIntArray byteToIntArray = new ByteToIntArray();
        Integer[] recArr = byteToIntArray.ByteToIntArray(byteArr);
        int fileID = recArr[3].intValue();
        if(fileID > 0){
            isDownload = true;
        }else{
            isDownload = false;
        }
        return isDownload;
    }

}
