package com.nedap.university.FileProtocol;

import com.nedap.university.client.UDPClient.UDPClient;

import java.awt.*;
import java.net.InetAddress;

/**
 * Created by yvo.romp on 13/04/2017.
 */
public class LSAction extends Action{

    public LSAction(UDPClient client, String s){
      performAction(client);
    }

    @Override
    public void performAction(UDPClient client){
        client.getCommandHandlerOfClient().sendLsMessage();
    }






}

