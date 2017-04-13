package com.nedap.university.UDPpackageStructure;

import com.nedap.university.client.UDPClient.UDPClient;
import com.nedap.university.client.UDPClient.commandHandlerOfClient;
import com.nedap.university.client.UDPClient.connection;
import com.nedap.university.server.UDPServer.UDPServer;
import com.nedap.university.server.UDPServer.commandHandlerOfServer;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
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
        packageDissector = serverHandler.getPackageD();
    }

    public void takeFlagActions(UDPServer UDPServer, DatagramPacket receivedPacket, InetAddress otherIPAddress, int clientPort, DatagramSocket UDPClientSocket){

        if(packageDissector.isBC){
            flags.setBC();
            flags.setACK();
            DatagramPacket returnPacket = serverHandler.makeDatagramPacket(otherIPAddress,clientPort,"hellotoyou");
            serverHandler.sendDatagramPacket(returnPacket);
        }
        if(packageDissector.isRequest){
            flags.setRequest();
            flags.setACK();
            DatagramPacket returnPacket = serverHandler.makeDatagramPacket(otherIPAddress,clientPort,"");
            serverHandler.sendDatagramPacket(returnPacket);
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
            } else if (packageDissector.isBC && packageDissector.isACK) {
                System.out.println("BC reply received!");
                createNewConnection(otherIPAddress,clientPort);
            } else {
                clientHandler.sendDatagramPacket(clientHandler.getDeepCopyPacket());
                System.out.println("resending package!");
            }
    }

    private void createNewConnection(InetAddress otherIP, int otherPort){
        connection newConnection = new connection(otherIP,otherPort);


    }
}
